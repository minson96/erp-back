package v1.erpback.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v1.erpback.board.domain.Board;
import v1.erpback.board.dto.BoardCreateDTO;
import v1.erpback.board.dto.BoardUpdateDTO;
import v1.erpback.board.repository.BoardMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public Long create(BoardCreateDTO boardCreateDTO) {
        Board board = Board.builder()
                .name(boardCreateDTO.getName())
                .companyId(boardCreateDTO.getCompanyId())
                .build();

        return boardMapper.create(board);
    }

    public List<Board> list(Long companyId) {
        return boardMapper.list(companyId);
    }

    public Long update(BoardUpdateDTO boardUpdateDTO) {
        Board board = Board.builder()
                .id(boardUpdateDTO.getId())
                .name(boardUpdateDTO.getName())
                .build();
        return boardMapper.update(board);
    }

    public void delete(List<Long> ids) {
        for (Long id : ids) {
            boardMapper.delete(id);
        }
    }
}
