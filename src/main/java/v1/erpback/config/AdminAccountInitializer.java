package v1.erpback.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import v1.erpback.user.domain.User;
import v1.erpback.user.repository.UserMapper;

@Component
@PropertySources({
        @PropertySource("classpath:env.properties")
})
public class AdminAccountInitializer implements CommandLineRunner {
    @Value("${password_key}")
    private String passwordKey;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userMapper.findByAccount("admin") == null) {
            User admin = User.builder()
                    .account("admin")
                    .role(User.Role.ADMIN)
                    .email("admin@example.com")
                    .password(passwordEncoder.encode(passwordKey))
                    .name("관리자")
                    .status(User.SignUpStatus.APPROVED)
                    .build();

            userMapper.signUp(admin);
            System.out.println("관리자 계정이 생성되었습니다: admin@example.com / 비밀번호: afafafaf");
        }
        else {
            System.out.println("관리자 계정이 이미 존재합니다.");
        }
    }
}
