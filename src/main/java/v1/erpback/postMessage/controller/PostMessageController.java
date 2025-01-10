package v1.erpback.postMessage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.postMessage.dto.*;
import v1.erpback.postMessage.service.PostMessageService;
import v1.erpback.user.domain.User;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.user.service.UserService;
import v1.erpback.util.PageResultDTO;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/postMessage")
public class PostMessageController {
    private final PostMessageService postMessageService;
    private final UserMapper userMapper;
    private final UserService userService;

    @PostMapping("/send")
    public ResponseEntity<String> send(@ModelAttribute PostMessageSendDTO postMessage) throws IOException {
        postMessageService.send(postMessage);
        return ResponseEntity.ok("전송이 완료되었습니다.");
    }

    @GetMapping("/sendList")
    public ResponseEntity<PageResultDTO<PostMessageSendListDTO>> sendList(@RequestHeader("Authorization")String header,
                                                                          @RequestParam(defaultValue = "1") int page,
                                                                          @RequestParam(defaultValue = "10") int size,
                                                                          @RequestParam(defaultValue = "") String search) throws IOException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(postMessageService.sendList(user.getId(), page, size, search));
    }

    @GetMapping("/receiveList")
    public ResponseEntity<PageResultDTO<PostMessageReceiveListDTO>> receiveList(@RequestHeader("Authorization")String header,
                                                                                @RequestParam(defaultValue = "1") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(defaultValue = "") String search) throws IOException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(postMessageService.receiveList(user.getId(), page, size, search));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization")String header, @RequestBody DeletePostMassagesDTO dto) {
        User user = userService.myInfo(header);
        System.out.println(dto.getIds());
        postMessageService.delete(user.getId(), dto);
        return ResponseEntity.ok("삭제가 완료되엇습니다.");
    }

    @PostMapping("/realDelete")
    public ResponseEntity<String> realDelete(@RequestHeader("Authorization")String header, @RequestBody DeletePostMassagesDTO dto) {
        User user = userService.myInfo(header);
        postMessageService.realDelete(user.getId(), dto);
        return ResponseEntity.ok("삭제가 완료되엇습니다.");
    }

    @GetMapping("/deleteList")
    public ResponseEntity<PageResultDTO<PostMessageDeleteListDTO>> deleteList(@RequestHeader("Authorization")String header,
                                                                              @RequestParam(defaultValue = "1") int page,
                                                                              @RequestParam(defaultValue = "10") int size,
                                                                              @RequestParam(defaultValue = "") String search) throws IOException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(postMessageService.deleteList(user.getId(), page, size, search));
    }

}
