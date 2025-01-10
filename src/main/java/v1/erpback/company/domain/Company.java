package v1.erpback.company.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Company {
    private Long id;
    private Status status;
    private String name;
    private String address;
    private Boolean fileAttached;
    private String originalFileName;
    private String storedFileName;
    public enum Status {
        ING, DELETE
    }
}
