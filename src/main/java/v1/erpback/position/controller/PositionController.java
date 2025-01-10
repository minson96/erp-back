package v1.erpback.position.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import v1.erpback.position.domain.JobPosition;
import v1.erpback.position.service.PositionService;

import javax.swing.text.Position;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/position")
public class PositionController {
    private final PositionService positionService;

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestParam Long companyId, @RequestParam String title ) {

        return ResponseEntity.ok(positionService.create(companyId, title));
    }

    @GetMapping("/list")
    public ResponseEntity<List<JobPosition>> list(@RequestParam Long companyId) {
        return ResponseEntity.ok().body(positionService.list(companyId));
    }

    @PostMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam Long companyId, @RequestParam Long positionId) {
        positionService.delete(companyId, positionId);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}
