package v1.erpback.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.post.domain.Post;
import v1.erpback.post.dto.*;
import v1.erpback.post.service.PostService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;
import v1.erpback.util.PageResultDTO;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
public class PostController {
    private final UserService userService;
    private final PostService postService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestHeader("Authorization") String header, @ModelAttribute PostCreateDTO dto) throws CustomException, IOException {
        User user = userService.myInfo(header);
        if (user.getRole() == User.Role.USER) throw new CustomException(ErrorCode.NOT_AUTHORITY_USER);
        postService.create(dto);
        return ResponseEntity.ok("생성이 완료되었습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<PageResultDTO<PostListDTO>> list(
                                                           @RequestParam Long companyId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "") String search) throws CustomException {
        return ResponseEntity.ok(postService.list(companyId, page, size, search));
    }

    @GetMapping("/detail")
    public ResponseEntity<PostDetailDTO> detail(@RequestHeader("Authorization") String header, @RequestParam Long postId) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok(postService.detail(postId));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String header, @RequestBody PostDeleteDTO postDeleteDTO)  throws CustomException, IOException {
        User user = userService.myInfo(header);
        if (user.getRole() == User.Role.USER) throw new CustomException(ErrorCode.NOT_AUTHORITY_USER);
        postService.delete(postDeleteDTO.getIds());
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }

    @PostMapping("/update")
    public ResponseEntity<String> update(@RequestHeader("Authorization") String header, @ModelAttribute PostUpdateDTO dto) throws CustomException, IOException {
        User user = userService.myInfo(header);
        if (user.getRole() == User.Role.USER) throw new CustomException(ErrorCode.NOT_AUTHORITY_USER);
        postService.update(dto);
        return ResponseEntity.ok("수정이 완료되었습니다.");
    }
}
