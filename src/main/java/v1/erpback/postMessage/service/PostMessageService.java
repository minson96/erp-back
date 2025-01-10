package v1.erpback.postMessage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.department.service.DepartmentService;
import v1.erpback.file.domain.FileDomain;
import v1.erpback.file.repository.FileMapper;
import v1.erpback.noti.repository.NotiMapper;
import v1.erpback.postMessage.domain.PostMessage;
import v1.erpback.postMessage.dto.*;
import v1.erpback.postMessage.repository.PostMessageMapper;
import v1.erpback.postMessage.repository.PostMessageReceiverMapper;
import v1.erpback.user.domain.User;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.util.PageResultDTO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostMessageService {
    private final PostMessageMapper postMessageMapper;
    private final PostMessageReceiverMapper postMessageReceiverMapper;
    private final UserMapper userMapper;
    private final FileMapper fileMapper;
    private final DepartmentMapper departmentMapper;

    private static final String FILE_SAVE_PATH = "C:/Users/vlrma/boardfile/test/";
    private final NotiMapper notiMapper;
    private final DepartmentService departmentService;

    public void send(PostMessageSendDTO postMessage) throws IOException {
        User user2 = userMapper.findById(postMessage.getUserId());

        String notiMessage = user2.getName() + "님께서 쪽지를 보내셨습니다.";

// Save the main post message
        PostMessage data = PostMessage.builder()
                .title(postMessage.getTitle())
                .userId(postMessage.getUserId())
                .content(postMessage.getContent())
                .fileAttached(postMessage.getFiles() != null && !postMessage.getFiles().isEmpty()) // Null 체크 추가
                .status(PostMessage.MessageStatus.ALIVE)
                .build();
        postMessageMapper.send(data);
        Long postMessageId = data.getId();

// Save receiver data for each department
        if (postMessage.getReceiverDepartmentIds() != null && !postMessage.getReceiverDepartmentIds().isEmpty()) {
            List<Long> receiverDepartmentIds = Arrays.stream(postMessage.getReceiverDepartmentIds().split(","))
                    .filter(id -> id != null && !id.trim().isEmpty()) // 빈 문자열 필터링
                    .map(Long::valueOf)
                    .toList();

            for (Long departmentId : receiverDepartmentIds) {
                List<Department> allSubDepts = departmentService.getAllSubDepartments(departmentId, new HashSet<>());
                for (Department department : allSubDepts) {
                    List<User> users = userMapper.findByDepartmentId(department.getId());
                    for (User user : users) {
                        saveReceiver(postMessageId, user.getId(), user.getName());
                        notiMapper.save(user.getId(), notiMessage);
                    }
                }

            }
        }

// Save receiver data for each user
        if (postMessage.getReceiverUserIds() != null && !postMessage.getReceiverUserIds().isEmpty()) {
            List<Long> receiverUserIds = Arrays.stream(postMessage.getReceiverUserIds().split(","))
                    .map(Long::valueOf)
                    .toList();
            for (Long userId : receiverUserIds) {
                User user = userMapper.findById(userId);
                saveReceiver(postMessageId, user.getId(), user.getName());
                notiMapper.save(user.getId(), notiMessage);
            }
        }

// Save attached files
        if (postMessage.getFiles() != null && !postMessage.getFiles().isEmpty()) {
            saveFiles(postMessage.getFiles(), postMessageId);
        }

    }

    private void saveReceiver(Long postMessageId, Long receiverId, String receiverName) {
        PostMessageReceiver receiver = PostMessageReceiver.builder()
                .postMessageId(postMessageId)
                .receiverId(receiverId)
                .receiverData(receiverName)
                .status(PostMessageReceiver.MessageStatus.ALIVE)
                .build();
        postMessageReceiverMapper.create(receiver);
    }

    private void saveFiles(List<MultipartFile> files, Long postMessageId) throws IOException {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String originalFileName = file.getOriginalFilename();
                    String storedFileName = System.currentTimeMillis() + "_" + originalFileName;
                    String savePath = FILE_SAVE_PATH + storedFileName;

                    // Ensure directory exists
                    File directory = new File(FILE_SAVE_PATH);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Save the file
                    file.transferTo(new File(savePath));

                    // Save metadata
                    FileDomain newFile = FileDomain.builder()
                            .postMessageId(postMessageId)
                            .originalFileName(originalFileName)
                            .storedFileName(storedFileName)
                            .build();
                    fileMapper.postMessageCreate(newFile);
                }
            }
        }
    }

    public PageResultDTO<PostMessageSendListDTO> sendList(Long id,int page, int size, String search) {
        int offset = (page - 1) * size;
        List<PostMessageSendListDTO> content = postMessageMapper.sendList(id, offset, size, search);
        long totalElements = postMessageMapper.sendListSize(id, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PageResultDTO<PostMessageReceiveListDTO> receiveList(Long id, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<PostMessageReceiveListDTO> content = postMessageMapper.receiveList(id, offset, size, search);
        long totalElements = postMessageMapper.receiveListSize(id, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PageResultDTO<PostMessageDeleteListDTO> deleteList(Long id, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<PostMessageDeleteListDTO> content = postMessageMapper.deleteList(id, offset, size, search);
        long totalElements = postMessageMapper.deleteListSize(id, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public void delete(Long id, DeletePostMassagesDTO dto) {
        for (Long massegeId : dto.getIds()) {


            PostMessage postMessage = postMessageMapper.findPostMessage(massegeId);

            if (postMessage.getUserId().equals(id)) {
                postMessageMapper.delete(id, massegeId);
            } else {
                postMessageMapper.receiverDelete(id, massegeId);
            }
        }
    }

    public void realDelete(Long id, DeletePostMassagesDTO dto) {
        for (Long massegeId : dto.getIds()) {
            PostMessage postMessage = postMessageMapper.findPostMessage(massegeId);
            if (postMessage.getUserId().equals(id)) {
                postMessageMapper.realDelete(id, massegeId);
            } else {
                postMessageMapper.realReceiverDelete(id, massegeId);
            }
        }
    }
}
