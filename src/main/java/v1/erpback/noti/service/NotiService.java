package v1.erpback.noti.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import v1.erpback.noti.domain.Noti;
import v1.erpback.noti.repository.NotiMapper;
import v1.erpback.postMessage.repository.PostMessageMapper;
import v1.erpback.postMessage.repository.PostMessageReceiverMapper;
import v1.erpback.room.repository.RoomMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotiService {
    private final NotiMapper notiMapper;
    private final RoomMapper roomMapper;
    private final PostMessageReceiverMapper postMessageReceiverMapper;
    private final PostMessageMapper postMessageMapper;
    private final SimpMessagingTemplate messagingTemplate;
    public List<Noti> list(Long id) {
        return notiMapper.list(id);
    }

    public void save(Long id, String message) {
        notiMapper.save(id, message);
    }

    public void read(Long id) {
        notiMapper.read(id);
    }
}

