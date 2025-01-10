package v1.erpback.file.domain;

import lombok.*;

import java.lang.management.LockInfo;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDomain {
    private Long id;
    private Long postId;
    private Long postMessageId;
    private Long scheduleId;
    private String originalFileName;
    private String storedFileName;
}
