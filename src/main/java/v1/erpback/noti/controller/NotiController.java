package v1.erpback.noti.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.noti.domain.Noti;
import v1.erpback.noti.service.NotiService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/noti")
public class NotiController {
    private final NotiService notiService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<Noti>> list(@RequestHeader("Authorization") String header) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(notiService.list(user.getId()));
    }
    @PostMapping("/read")
    public ResponseEntity<String> read(@RequestHeader("Authorization") String header) {
        User user = userService.myInfo(header);
        notiService.read(user.getId());
        return ResponseEntity.ok().body("ok");
    }
}
