package v1.erpback.email.repository;

import io.micrometer.observation.ObservationFilter;
import org.apache.ibatis.annotations.*;
import v1.erpback.email.VerificationCode;

import java.time.LocalDateTime;
import java.util.Optional;

@Mapper
public interface EmailMapper {
    @Select("SELECT email, codes, expires_time FROM verification_codes WHERE email = #{email} AND codes = #{codes}")
    VerificationCode findByEmailAndCode(@Param("email") String email, @Param("codes") String codes);

    @Delete("DELETE FROM verification_codes WHERE expires_time < #{now}")
    void deleteByExpiresTimeBefore(LocalDateTime now);

    @Insert("INSERT INTO verification_codes (email, codes, expires_time)"
            + "VALUES (#{email}, #{codes}, #{expiresTime})")
    void save(VerificationCode code);

    @Delete("DELETE FROM verification_code WHERE verification_code = #{verificationCode}")
    void deleteUseCode(String verificationCode);
}
