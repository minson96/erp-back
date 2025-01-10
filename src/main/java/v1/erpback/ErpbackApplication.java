package v1.erpback;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ErpbackApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErpbackApplication.class, args);
	}

}
