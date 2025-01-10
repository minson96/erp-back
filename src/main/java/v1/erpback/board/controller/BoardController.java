package v1.erpback.board.controller;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.auth.domain.Jwt;
import v1.erpback.auth.service.JwtTokenService;
import v1.erpback.board.domain.Board;
import v1.erpback.board.dto.BoardCreateDTO;
import v1.erpback.board.dto.BoardDeleteDTO;
import v1.erpback.board.dto.BoardUpdateDTO;
import v1.erpback.board.service.BoardService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;
    private final UserService userService;
    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestHeader("Authorization") String header, @RequestBody BoardCreateDTO boardCreateDTO) throws CustomException {
        User user = userService.myInfo(header);
        if (user.getRole() == User.Role.USER) throw new CustomException(ErrorCode.NOT_AUTHORITY_USER);
        return ResponseEntity.ok(boardService.create(boardCreateDTO));
    }

    @GetMapping("/list")
    public ResponseEntity<List<Board>> list(@RequestHeader("Authorization") String header, @RequestParam Long companyId) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(boardService.list(companyId));
    }

    @PostMapping("/update")
    public ResponseEntity<Long> update(@RequestHeader("Authorization") String header, @RequestBody BoardUpdateDTO boardUpdateDTO) throws CustomException {
        User user = userService.myInfo(header);
        if (user.getRole() == User.Role.USER) throw new CustomException(ErrorCode.NOT_AUTHORITY_USER);
        return ResponseEntity.ok(boardService.update(boardUpdateDTO));
    }

    @PostMapping("/delete")
    public void delete(@RequestHeader("Authorization") String header, @RequestBody BoardDeleteDTO boardDeleteDTO) throws CustomException {
        User user = userService.myInfo(header);
        if (user.getRole() == User.Role.USER) throw new CustomException(ErrorCode.NOT_AUTHORITY_USER);
        boardService.delete(boardDeleteDTO.getIds());
    }

}
