package v1.erpback.position.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v1.erpback.position.domain.JobPosition;
import v1.erpback.position.repository.PositionMapper;

import javax.swing.text.Position;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionService {
    private final PositionMapper positionMapper;

    public Long create(Long companyId, String title) {
        return positionMapper.create(companyId, title);
    }

    public List<JobPosition> list(Long companyId) {
        return positionMapper.list(companyId);
    }

    public void delete(Long companyId, Long positionId) {
        positionMapper.delete(companyId, positionId);
    }
}
