package v1.erpback.room.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.department.service.DepartmentService;
import v1.erpback.room.domain.ChatUser;
import v1.erpback.room.domain.Room;
import v1.erpback.room.dto.MessageLoadDTO;
import v1.erpback.room.dto.RoomListDTO;
import v1.erpback.room.repository.RoomMapper;
import v1.erpback.user.domain.User;
import v1.erpback.user.repository.UserMapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomMapper roomMapper;
    private final UserMapper userMapper;
    private final DepartmentMapper departmentMapper;
    private final DepartmentService departmentService;

    public List<RoomListDTO> list(User user) {
        return roomMapper.list(user.getId());
    }

    public List<MessageLoadDTO> loadRoom(Long roomId) {
        return roomMapper.loadRoom(roomId);
    }

    public void create(Long id, String roomName) {
        Room room = Room.builder()
                .name(roomName)
                .isGroup(false)
                .build();
        roomMapper.create(room);
        Long roomId = room.getId();

        roomMapper.join(roomId, id);
    }

    public void invite(Long roomId, Long userId) {
        if (roomMapper.findByRoomAndMe(roomId, userId) == null) {
            roomMapper.join(roomId, userId);
        }
    }

    public void delete(Long id, Long roomId) {
        // + 방에 사람이 0명이면 방 삭제
        roomMapper.leave(id, roomId);
        if (roomMapper.countRoomUser(roomId) == 0) {
            roomMapper.delete(roomId);
        }

    }

    public Room findById(Long roomId) {
        return roomMapper.findById(roomId);
    }

    public List<Long> findRoomUsers(Long roomId) {
        return roomMapper.findRoomUsers(roomId);
    }

    public void inviteUsers(Long id, List<Long> userIds) {
        StringBuilder newRoomName = new StringBuilder();
        for (Long userId : userIds) {
            User user = userMapper.findById(userId);
            newRoomName.append(", ").append(user.getName());
        }

        Room room = Room.builder()
                .name(newRoomName.toString())
                .isGroup(true)
                .build();

        roomMapper.create(room);
        Long roomId = room.getId();
        roomMapper.join(roomId, id);
        for (Long userId : userIds) {
            roomMapper.join(roomId, userId);
        }
    }


    public void inviteDepartments(Long id, List<Long> departmentsIds) {
        // 1. 룸 이름 생성
        StringBuilder newRoomName = new StringBuilder();
        for (Long departmentId : departmentsIds) {
            Department department = departmentMapper.findById(departmentId);
            newRoomName.append(", ").append(department.getName());
        }
        Room room = Room.builder()
                .name(newRoomName.toString())
                .isGroup(true)
                .build();

        // 2. 룸 생성
        roomMapper.create(room);
        Long roomId = room.getId();

        Set<Long> userIds = new HashSet<>();
        // 3. 하위 부서 모두 탐색 & 해당 부서에 속한 유저 초대
        //    (예: 재귀 메서드나 BFS 메서드를 이용)
        for (Long departmentId : departmentsIds) {
            // departmentId 자신도 포함해야 한다면 별도 로직 추가 필요
            List<Department> allSubDepts = departmentService.getAllSubDepartments(departmentId, new HashSet<>());
            for (Department department : allSubDepts) {
                List<User> users = userMapper.findByDepartmentId(department.getId());
                for (User user : users) {
                    roomMapper.join(roomId, user.getId());
                }
            }
        }

        // 4. 방을 만든 유저(혹은 초대 주체)가 아직 방에 없으면 추가
        if (roomMapper.findByRoomAndMe(roomId, id) == null) {
            roomMapper.join(roomId, id);
        }
    }
}
