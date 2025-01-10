package v1.erpback.company.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CompanyCreateDTO {
    private String name;
    private String address;
    private MultipartFile logoImg;
}
