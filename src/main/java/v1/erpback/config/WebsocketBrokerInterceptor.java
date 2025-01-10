package v1.erpback.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import v1.erpback.auth.util.JwtTokenVerifier;

import java.util.List;
import java.util.Map;

@Component
public class WebsocketBrokerInterceptor implements ChannelInterceptor {

    @Autowired
    private JwtTokenVerifier jwtTokenVerifier;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        StompCommand command = accessor.getCommand();
        if (command == null) {
            System.out.println("STOMP command is null. Skipping message.");
            return message;
        }

        switch (command) {
            case CONNECT:
                validateConnect(accessor);
                break;
            case SUBSCRIBE:
                validateSubscribe(accessor);
                break;
            case SEND:
                validateSend(accessor);
                break;
            default:
                break;
        }

        return message;
    }

    private void validateConnect(StompHeaderAccessor accessor) {
        String token = extractAuthToken(accessor);
        if (token == null || !isValidToken(token)) {
            throw new IllegalArgumentException("Invalid token during WebSocket connection.");
        }
    }

    private void validateSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !isValidDestination(destination)) {
            throw new IllegalArgumentException("Invalid subscription destination.");
        }
    }

    private void validateSend(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination == null || !isAuthorizedToSend(destination)) {
            throw new IllegalArgumentException("Unauthorized send destination.");
        }
    }

    private String extractAuthToken(StompHeaderAccessor accessor) {
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        return null;
    }

    private boolean isValidToken(String token) {
        try {
            jwtTokenVerifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    private boolean isValidDestination(String destination) {
        return destination.startsWith("/sub/");
    }

    private boolean isAuthorizedToSend(String destination) {
        return destination.startsWith("/pub/");
    }
}
