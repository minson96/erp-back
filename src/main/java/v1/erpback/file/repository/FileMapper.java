package v1.erpback.file.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import v1.erpback.file.domain.FileDomain;

@Mapper
public interface FileMapper {
    @Insert("INSERT INTO file (post_id, original_file_name, stored_file_name)" +
            "VALUES (#{postId}, #{originalFileName}, #{storedFileName})")
    void create(FileDomain newFile);

    @Insert("INSERT INTO file (original_file_name, stored_file_name, post_message_id)" +
            "VALUES (#{originalFileName}, #{storedFileName}, #{postMessageId})")
    void postMessageCreate(FileDomain newFile);

    @Insert("INSERT INTO file (original_file_name, stored_file_name, schedule_id)" +
            "VALUES (#{originalFileName}, #{storedFileName}, #{scheduleId})")
    void scheduleCreate(FileDomain newFile);

    @Delete("DELETE FROM file WHERE post_id = #{postId}")
    void deleteAll(Long postId);
}
