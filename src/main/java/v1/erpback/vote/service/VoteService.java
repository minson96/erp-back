package v1.erpback.vote.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import v1.erpback.Exception.CustomException;
import v1.erpback.Exception.ErrorCode.ErrorCode;
import v1.erpback.department.domain.Department;
import v1.erpback.department.repository.DepartmentMapper;
import v1.erpback.department.service.DepartmentService;
import v1.erpback.user.domain.User;
import v1.erpback.user.dto.AdminsUserListDTO;
import v1.erpback.user.repository.UserMapper;
import v1.erpback.util.PageResultDTO;
import v1.erpback.vote.domain.Vote;
import v1.erpback.vote.domain.VoteResult;
import v1.erpback.vote.domain.VoteUser;
import v1.erpback.vote.dto.VoteCreateDTO;
import v1.erpback.vote.dto.VoteDetailDTO;
import v1.erpback.vote.dto.VoteListDTO;
import v1.erpback.vote.repository.VoteMapper;
import v1.erpback.vote.repository.VoteResultMapper;
import v1.erpback.vote.repository.VoteUserMapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteMapper voteMapper;
    private final VoteUserMapper voteUserMapper;
    private final VoteResultMapper voteResultMapper;
    private final DepartmentMapper departmentMapper;
    private final UserMapper userMapper;
    private final DepartmentService departmentService;

    public void create(Long id, VoteCreateDTO voteCreateDTO) {
        Vote vote = Vote.builder()
                .title(voteCreateDTO.getTitle())
                .startDateTime(voteCreateDTO.getStartDateTime())
                .endDateTime(voteCreateDTO.getEndDateTime())
                .status(Vote.Status.PENDING)
                .userId(id)
                .build();
        voteMapper.create(vote);

        Long voteId = vote.getId();

// 2) 유저 ID를 담을 Set 준비
        Set<Long> userIds = new HashSet<>();

// 3) DEPARTMENT 등록
        for (Long departmentId : voteCreateDTO.getDepartmentId()) {
            // DEPARTMENT 자체를 insert
            voteUserMapper.voteCreateDepartment(departmentId, voteId, "DEPARTMENT");

            // 모든 하위 부서 조회
            List<Department> allSubDepts = departmentService.getAllSubDepartments(departmentId, new HashSet<>());
            for (Department department : allSubDepts) {
                // 각 부서에 속한 유저들 조회
                List<User> users = userMapper.findByDepartmentId(department.getId());
                for (User user : users) {
                    // 중복 없이 Set에 추가만 함 (INSERT는 아직 하지 않음)
                    userIds.add(user.getId());
                }
            }
        }

// 4) voteCreateDTO.getUserId() 목록도 Set에 추가
        for (Long userId : voteCreateDTO.getUserId()) {
            userIds.add(userId);
        }

// 5) 투표 생성한 유저 본인이 포함되어 있는지 확인
        if (voteUserMapper.isVoted(id, voteId) == null) {
            userIds.add(id);
        }

// 6) 최종적으로 userIds에 있는 유저들을 INSERT IGNORE
//    (또는 중복이면 무시할 수 있도록 DB에 (user_id, vote_id) 복합 UNIQUE 인덱스 적용)
        for (Long uId : userIds) {
            voteUserMapper.voteCreateUser(uId, voteId, "USER");
        }

// 7) 선택지(VoteResult) 등록
        for (String answer : voteCreateDTO.getChoices()) {
            VoteResult voteResult = VoteResult.builder()
                    .voteId(voteId)
                    .answer(answer)
                    .build();
            voteResultMapper.create(voteResult);
        }
    }

    public void vote(Long id, Long voteId, String answer) throws CustomException {
        System.out.println(id +"---------" + voteId);
        VoteUser voteUser = voteUserMapper.isVoted(id, voteId);
        VoteResult voteResult= voteResultMapper.findAnswer(voteId, answer);
        voteUserMapper.vote(voteUser.getId(), voteResult.getId());

    }

    public void result(Long voteId) {
        List<VoteResult> countAnswer = voteResultMapper.countAnswer(voteId);
        for (VoteResult result : countAnswer) {
            Long countAnswerUser = voteUserMapper.countUser(voteId, result.getId());
            voteResultMapper.result(voteId, countAnswerUser, result.getId());
        }
        voteMapper.voteEnd(voteId);
    }

    public PageResultDTO<VoteListDTO> list(User user, int page, int size, String search) {
        int offset = (page - 1) * size;
        System.out.println("offset = " + offset);
        List<VoteListDTO> content = voteMapper.list(user, offset, size, search);
        long totalElements = voteMapper.listCount(user, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PageResultDTO<VoteListDTO> resultList(User user, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<VoteListDTO> content = voteMapper.resultList(user, offset, size, search);
        long totalElements = voteMapper.resultListCount(user, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public PageResultDTO<VoteListDTO> myList(User user, int page, int size, String search) {
        int offset = (page - 1) * size;
        List<VoteListDTO> content = voteMapper.myList(user ,offset, size, search);
        long totalElements = voteMapper.myListCount(user, search);
        return new PageResultDTO<>(content, totalElements, page, size);
    }

    public VoteDetailDTO detail(Long userId,Long voteId) {
        VoteDetailDTO voteDetailDTO = voteMapper.detail(userId,voteId);
        System.out.println(voteDetailDTO.getUserSelect());
        return voteMapper.detail(userId, voteId);
    }
}
