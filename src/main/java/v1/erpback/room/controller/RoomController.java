package v1.erpback.room.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.room.domain.Room;
import v1.erpback.room.dto.*;
import v1.erpback.room.service.RoomService;
import v1.erpback.user.domain.User;
import v1.erpback.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/room")
public class RoomController {
    private final RoomService roomService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<List<RoomListDTO>> list(@RequestHeader("Authorization") String header) {
        User user = userService.myInfo(header);
        return ResponseEntity.ok().body(roomService.list(user));
    }

    @GetMapping("/loadChat")
    public ResponseEntity<List<MessageLoadDTO>> loadChat(@RequestParam Long roomId) {
        return ResponseEntity.ok().body(roomService.loadRoom(roomId));
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestHeader("Authorization") String header, @RequestParam String roomName) {
        User user = userService.myInfo(header);
        roomService.create(user.getId(), roomName);
        return ResponseEntity.ok("채팅방이 생성되었습니다.");
    }

    @PostMapping("/invite")
    public ResponseEntity<String> invite(@RequestHeader("Authorization") String header,
                                         @RequestBody InviteRoomDTO inviteRoomDTO
                                         ) {
        User user = userService.myInfo(header);
        System.out.println(inviteRoomDTO.getRoomId() + "----------" + inviteRoomDTO.getUserId());
        roomService.invite(inviteRoomDTO.getRoomId(), inviteRoomDTO.getUserId());
        return ResponseEntity.ok("초대가 완료되었습니다.");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestHeader("Authorization") String header, @RequestBody RoomDeleteDTO roomDeleteDTO) {
        User user = userService.myInfo(header);
        roomService.delete(user.getId(), roomDeleteDTO.getRoomId());
        return ResponseEntity.ok("퇴장하였습니다.");
    }

    @PostMapping("/inviteUsers")
    public ResponseEntity<String> inviteUsers(@RequestHeader("Authorization") String header, @RequestBody RoomInviteUsersDTO dto) {
        User user = userService.myInfo(header);
        roomService.inviteUsers(user.getId(),dto.getUserIds());

        return ResponseEntity.ok("초대가 완료되었습니다.");
    }

    @PostMapping("/inviteDepartments")
    public ResponseEntity<String> inviteDepartments(@RequestHeader("Authorization") String header, @RequestBody RoomInviteDepartmentsDTO dto) {
        User user = userService.myInfo(header);
        roomService.inviteDepartments(user.getId(),dto.getDepartmentsIds());

        return ResponseEntity.ok("초대가 완료되었습니다.");
    }
}
