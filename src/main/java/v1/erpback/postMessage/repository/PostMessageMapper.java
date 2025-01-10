package v1.erpback.postMessage.repository;

import org.apache.ibatis.annotations.*;
import v1.erpback.postMessage.domain.PostMessage;
import v1.erpback.postMessage.dto.PostMessageDeleteListDTO;
import v1.erpback.postMessage.dto.PostMessageReceiveListDTO;
import v1.erpback.postMessage.dto.PostMessageSendListDTO;

import java.util.List;

@Mapper
public interface PostMessageMapper {
    @Insert("INSERT INTO post_message (user_id, title, content, file_attached, status) VALUES (#{userId}, #{title}, #{content}, #{fileAttached}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long send(PostMessage data);

    @Select("""
    SELECT pm.id, pm.title, pm.content, pm.created_at,
           GROUP_CONCAT( pr.receiver_data SEPARATOR ', ') AS receivers,
           GROUP_CONCAT( f.stored_file_name SEPARATOR ', ') AS files
    FROM post_message pm
    LEFT JOIN post_message_receiver pr ON pm.id = pr.post_message_id
    LEFT JOIN file f ON pm.id = f.post_message_id
    WHERE pm.user_id = #{id} AND pm.status = 'ALIVE' AND (pm.title LIKE CONCAT('%', #{search}, '%') OR pr.receiver_data LIKE CONCAT('%', #{search}, '%'))
    GROUP BY pm.id, pm.title, pm.content, pm.created_at
    ORDER BY pm.created_at DESC
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results ({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "receivers", column = "receivers"),
            @Result(property = "content", column = "content"),
            @Result(property = "files", column = "files")
    })
    List<PostMessageSendListDTO> sendList(@Param("id") Long id,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit,
                                          @Param("search") String search);

    @Select("""
    SELECT COUNT(DISTINCT pm.id)
    FROM post_message pm
    LEFT JOIN post_message_receiver pr ON pm.id = pr.post_message_id
    WHERE pm.user_id = #{id} AND pm.status = 'ALIVE' AND (pm.title LIKE CONCAT('%', #{search}, '%') OR pr.receiver_data LIKE CONCAT('%', #{search}, '%'))
""")
    long sendListSize(@Param("id") Long id, @Param("search") String search);

    @Select("""
    SELECT pm.id, pm.title, pm.content, pm.created_at, pm.user_id, u.name, GROUP_CONCAT( f.stored_file_name SEPARATOR ', ') AS files
    FROM post_message pm
    INNER JOIN post_message_receiver pr ON pm.id = pr.post_message_id
    LEFT JOIN file f ON pm.id = f.post_message_id
    LEFT JOIN user u ON pm.user_id = u.id
    WHERE pr.receiver_id = #{id} AND pr.status = 'ALIVE' AND (pm.title LIKE CONCAT('%', #{search}, '%') OR u.name LIKE CONCAT('%', #{search}, '%'))
    GROUP BY pm.id, pm.title, pm.content, pm.created_at, pm.user_id, u.name
    ORDER BY pm.created_at DESC
    LIMIT #{limit} OFFSET #{offset}
""")
    @Results ({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "sender", column = "name"),
            @Result(property = "content", column = "content"),
            @Result(property = "files", column = "files")
    })
    List<PostMessageReceiveListDTO> receiveList(Long id, int offset, int limit, String search);

    @Select("""
    SELECT COUNT(*)
    FROM post_message pm
    INNER JOIN post_message_receiver pr ON pm.id = pr.post_message_id
    LEFT JOIN user u ON pm.user_id = u.id
    WHERE pr.receiver_id = #{id} AND pm.status = 'ALIVE' AND (pm.title LIKE CONCAT('%', #{search}, '%') OR u.name LIKE CONCAT('%', #{search}, '%'))
""")
    long receiveListSize(Long id, String search);


    @Select("""
  (SELECT pm.id, pm.title, pm.content, pm.created_at, pr.deleted_at, pm.user_id, u.name AS sender,
          GROUP_CONCAT(DISTINCT pr.receiver_data SEPARATOR ', ') AS receivers,
          GROUP_CONCAT(DISTINCT f.stored_file_name SEPARATOR ', ') AS files,
          'RECEIVE' AS message_type
   FROM post_message pm
   INNER JOIN post_message_receiver pr ON pm.id = pr.post_message_id
   LEFT JOIN file f ON pm.id = f.post_message_id
   LEFT JOIN user u ON pm.user_id = u.id
   WHERE pr.receiver_id = #{id}
     AND pr.status = 'TRASH'
     AND (pm.title LIKE CONCAT('%', #{search}, '%') 
          OR u.name LIKE CONCAT('%', #{search}, '%'))
   GROUP BY pm.id, pm.title, pm.content, pm.created_at, pr.deleted_at, pm.user_id, u.name)

  UNION

  (SELECT pm.id, pm.title, pm.content, pm.created_at, pm.deleted_at, pm.user_id, u.name AS sender,
          GROUP_CONCAT(DISTINCT pr.receiver_data SEPARATOR ', ') AS receivers,
          GROUP_CONCAT(DISTINCT f.stored_file_name SEPARATOR ', ') AS files,
          'SEND' AS message_type
   FROM post_message pm
   LEFT JOIN post_message_receiver pr ON pm.id = pr.post_message_id
   LEFT JOIN file f ON pm.id = f.post_message_id
   LEFT JOIN user u ON pm.user_id = u.id
   WHERE pm.user_id = #{id}
     AND pm.status = 'TRASH'
     AND (pm.title LIKE CONCAT('%', #{search}, '%') 
          OR pr.receiver_data LIKE CONCAT('%', #{search}, '%'))
   GROUP BY pm.id, pm.title, pm.content, pm.created_at, pm.deleted_at, pm.user_id, u.name)

  ORDER BY created_at DESC
  LIMIT #{limit} OFFSET #{offset}
""")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "title", column = "title"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "deletedAt", column = "deleted_at"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "sender", column = "sender"),
            @Result(property = "receivers", column = "receivers"),
            @Result(property = "content", column = "content"),
            @Result(property = "files", column = "files"),
            @Result(property = "messageType", column = "message_type")
    })
    List<PostMessageDeleteListDTO> deleteList(@Param("id") Long id,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit,
                                      @Param("search") String search);

    @Select("""
  SELECT SUM(cnt) AS total_count
  FROM (
    SELECT COUNT(*) AS cnt
    FROM post_message pm
   INNER JOIN post_message_receiver pr ON pm.id = pr.post_message_id
   LEFT JOIN file f ON pm.id = f.post_message_id
   LEFT JOIN user u ON pm.user_id = u.id
   WHERE pr.receiver_id = #{id}
     AND pr.status = 'TRASH'
     AND (pm.title LIKE CONCAT('%', #{search}, '%') 
          OR u.name LIKE CONCAT('%', #{search}, '%'))
    
    UNION ALL
    
    SELECT COUNT(*) AS cnt
    FROM post_message pm
   LEFT JOIN post_message_receiver pr ON pm.id = pr.post_message_id
   LEFT JOIN file f ON pm.id = f.post_message_id
   LEFT JOIN user u ON pm.user_id = u.id
   WHERE pm.user_id = #{id}
     AND pm.status = 'TRASH'
     AND (pm.title LIKE CONCAT('%', #{search}, '%') 
          OR pr.receiver_data LIKE CONCAT('%', #{search}, '%'))
  ) AS combined
""")
    long deleteListSize(Long id, String search);

    @Update("""
    UPDATE
        post_message pm
    JOIN
        post_message_receiver pr ON pm.id = pr.post_message_id
    SET
        pm.deleted_at = CURRENT_TIMESTAMP , pm.status = 'TRASH'
    WHERE
        pm.id = #{messageId} AND pm.user_id = #{id}
""")
    void delete(Long id, Long messageId);

    @Update("""
    UPDATE
        post_message_receiver pr
    JOIN
        post_message pm ON pm.id = pr.post_message_id
    SET
        pr.deleted_at = CURRENT_TIMESTAMP , pr.status = 'TRASH'
    WHERE
        pr.post_message_id = #{messageId} AND pr.receiver_id = #{id}
""")
    void receiverDelete(Long id, Long messageId);


    @Update("UPDATE post_message SET status = 'DELETE' WHERE id = #{messageId} AND user_id = #{id}")
    void realDelete(Long id, Long messageId);

    @Update("UPDATE post_message_receiver SET status = 'DELETE' WHERE post_message_id = #{messageId} AND receiver_id = #{id}")
    void realReceiverDelete(Long id, Long messageId);

    @Select("SELECT id, user_id FROM post_message WHERE id = #{massegeId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id")
    })
    PostMessage findPostMessage(Long massegeId);

    @Update("""
    UPDATE post_message 
    SET status = 'DELETE' 
    WHERE deleted_at <= NOW() - INTERVAL 30 DAY
""")
    void autoDelete();
    @Update("""
    UPDATE post_message_receiver
    SET status = 'DELETE' 
    WHERE deleted_at <= NOW() - INTERVAL 30 DAY
""")
    void autoReceiverDelete();
}
