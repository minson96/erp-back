package v1.erpback.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class CustomWebSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 연결이 설정된 후의 로직
        System.out.println("WebSocket 연결이 설정되었습니다: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 메시지 처리 로직
        System.out.println("수신된 메시지: " + message.getPayload());
        // 필요에 따라 메시지를 처리하고 응답을 보낼 수 있습니다.
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        // 오류 처리 로직
        String reason = "서버 내부 오류가 발생했습니다.";
        if (!isValidUTF8(reason)) {
            reason = "서버 오류"; // 기본 사유로 대체
        }
        session.close(new CloseStatus(1002, reason));
        System.err.println("WebSocket 오류 발생: " + exception.getMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 연결이 종료된 후의 로직
        System.out.println("WebSocket 연결이 종료되었습니다: " + session.getId() + ", 상태: " + status);
    }

    /**
     * UTF-8 유효성 검증 함수
     */
    private boolean isValidUTF8(String str) {
        try {
            byte[] bytes = str.getBytes("UTF-8");
            String decoded = new String(bytes, "UTF-8");
            return str.equals(decoded);
        } catch (Exception e) {
            return false;
        }
    }
}
