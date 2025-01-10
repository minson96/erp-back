package v1.erpback.room.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.room.domain.ChatUser;
import v1.erpback.room.domain.Room;
import v1.erpback.room.dto.MessageLoadDTO;
import v1.erpback.room.dto.RoomListDTO;

import java.util.List;

@Mapper
public interface RoomMapper {
    @Select("""
    SELECT
        cr.id,
        cr.name,
        cr.is_group,
        cr.created_at
    FROM
        chat_room cr
    LEFT JOIN chat_user cu ON cr.id = cu.room_id
    WHERE cu.user_id = #{id}
""")
    @Results({
            @Result(property = "id",column = "id"),
            @Result(property = "name",column = "name"),
            @Result(property = "isGroup",column = "is_group"),
            @Result(property = "createdAt",column = "created_at")
    })
    List<RoomListDTO> list(Long id);

    @Select("""
    SELECT
        cm.id, u.name AS user_name, cm.content, cm.created_at, cm.room_id
    FROM
        chat_message cm
    LEFT JOIN user u ON cm.sender_id = u.id
    WHERE
        room_id = #{roomId}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userName",column = "user_name"),
            @Result(property = "roomId",column = "room_id"),
            @Result(property = "message",column = "content"),
            @Result(property = "createdAt",column = "created_at")
    })
    List<MessageLoadDTO> loadRoom(Long roomId);

    @Insert("INSERT INTO chat_room (name, is_group) VALUES (#{name}, #{isGroup})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long create(Room room);

    @Insert("INSERT INTO chat_user (room_id, user_id) VALUES (#{roomId}, #{id})")
    void join(Long roomId, Long id);


    @Delete("DELETE FROM chat_user WHERE room_id = #{roomId} AND user_id = #{id}")
    void leave(Long id, Long roomId);

    @Select("""
    SELECT
        COUNT(*)
    FROM
        chat_user
    WHERE
        room_id = #{roomId}
""")
    Long countRoomUser(Long roomId);

    @Delete("DELETE FROM chat_room WHERE id = #{roomId}")
    void delete(Long roomId);

    @Select("SELECT * FROM chat_room WHERE id = #{roomId}")
    Room findById(Long roomId);

    @Select("SELECT user_id FROM chat_user WHERE room_id = #{roomId}"
    )
    List<Long> findRoomUsers(Long roomId);

    @Select("SELECT id, room_id, user_id, joined_at FROM chat_user WHERE room_id = #{roomId} AND user_id=#{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "roomId", column = "room_id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "joinedAt", column = "joined_at")
    })
    Long findByRoomAndMe(Long roomId, Long id);
}
