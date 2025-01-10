package v1.erpback.vote.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.Exception.CustomException;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;
import v1.erpback.util.PageResultDTO;
import v1.erpback.vote.dto.VoteCreateDTO;
import v1.erpback.vote.dto.VoteDTO;
import v1.erpback.vote.dto.VoteDetailDTO;
import v1.erpback.vote.dto.VoteListDTO;
import v1.erpback.vote.service.VoteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vote")
public class VoteController {
    private final VoteService voteService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestHeader("Authorization")String header, @RequestBody VoteCreateDTO voteCreateDTO) {
        User user = userService.myInfo(header);
        voteService.create(user.getId(), voteCreateDTO);
        return ResponseEntity.ok("등록이 완료되었습니다.");
    }

    @PostMapping("/vote")
    public ResponseEntity<String> vote(@RequestHeader("Authorization")String header, @RequestBody VoteDTO voteDTO) throws CustomException {
        User user = userService.myInfo(header);
        voteService.vote(user.getId(), voteDTO.getVoteId(), voteDTO.getAnswer());
        return ResponseEntity.ok("투표가 완료되었습니다.");
    }

    @PostMapping("/result")
    public ResponseEntity<String> result(@RequestParam Long voteId) throws CustomException {
        voteService.result(voteId);
        return ResponseEntity.ok("투표 결과가 나왔습니다.");
    }

    @GetMapping("/list")
    public ResponseEntity<PageResultDTO<VoteListDTO>> list(@RequestHeader("Authorization")String header,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "") String search) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(voteService.list(user, page, size, search));
    }

    @GetMapping("/resultList")
    public ResponseEntity<PageResultDTO<VoteListDTO>> resultList(@RequestHeader("Authorization")String header,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "10") int size,
                                                           @RequestParam(defaultValue = "") String search) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(voteService.resultList(user, page, size, search));
    }

    @GetMapping("/mylist")
    public ResponseEntity<PageResultDTO<VoteListDTO>> myList(@RequestHeader("Authorization")String header,
                                                                 @RequestParam(defaultValue = "1") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(defaultValue = "") String search) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(voteService.myList(user, page, size, search));
    }


    @GetMapping("/detail")
    public ResponseEntity<VoteDetailDTO> detail(@RequestHeader("Authorization")String header, @RequestParam Long voteId) throws CustomException {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(voteService.detail(user.getId(), voteId));
    }
}
