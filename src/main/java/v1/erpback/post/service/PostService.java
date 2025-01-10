package v1.erpback.post.service;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import v1.erpback.file.domain.FileDomain;
import v1.erpback.file.repository.FileMapper;
import v1.erpback.post.domain.Post;
import v1.erpback.post.dto.PostCreateDTO;
import v1.erpback.post.dto.PostDetailDTO;
import v1.erpback.post.dto.PostListDTO;
import v1.erpback.post.dto.PostUpdateDTO;
import v1.erpback.post.repository.PostMapper;
import v1.erpback.user.domain.User;
import v1.erpback.util.PageResultDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostMapper postMapper;
    private final FileMapper fileMapper;

    public void create(PostCreateDTO dto) throws IOException {

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .boardId(dto.getBoardId())
                .userId(dto.getUserId())
                .build();
        postMapper.create(post);

        Long postId = post.getId();
        if (dto.getFiles().get(0) != null) {
            for (MultipartFile file : dto.getFiles()) {
                String fileName = file.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + fileName;
                String savePath = "C:/Users/vlrma/boardfile/test/" + storedFileName;
                file.transferTo(new File(savePath));

                FileDomain newFile = FileDomain.builder()
                        .postId(postId)
                        .originalFileName(fileName)
                        .storedFileName(storedFileName)
                        .build();

                fileMapper.create(newFile);
            }
        }

    }

    public PageResultDTO<PostListDTO> list(Long companyId, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<PostListDTO> content = postMapper.list(companyId,offset, size, search);
        long totalElements = postMapper.countList(companyId, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PostDetailDTO detail(Long postId) {
        return postMapper.detail(postId);
    }

    public void delete(List<Long> ids) {
        for (Long id : ids) {
            postMapper.delete(id);
        }
    }

    public void update(PostUpdateDTO dto) throws IOException {
        Post post = Post.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .boardId(dto.getBoardId())
                .build();
        postMapper.update(post);

        Long postId = post.getId();
        if (dto.getFiles() != null && dto.getFiles().get(0) != null) {
            fileMapper.deleteAll(postId);
            for (MultipartFile file : dto.getFiles()) {
                String fileName = file.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + fileName;
                String savePath = "C:/Users/vlrma/boardfile/test/" + storedFileName;
                file.transferTo(new File(savePath));

                FileDomain newFile = FileDomain.builder()
                        .postId(postId)
                        .originalFileName(fileName)
                        .storedFileName(storedFileName)
                        .build();

                fileMapper.create(newFile);
            }
        }
    }
}
