package v1.erpback.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import v1.erpback.auth.service.JwtTokenService;
import v1.erpback.auth.util.JwtTokenVerifier;

import java.util.List;
import java.util.Map;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private JwtTokenVerifier jwtTokenVerifier;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String authHeader = request.getHeaders().getFirst("Authorization");
        System.out.println("Authorization Header: " + authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                DecodedJWT decodedJWT = jwtTokenVerifier.verify(token);
                System.out.println("Decoded JWT Subject: " + decodedJWT.getSubject());
                System.out.println("Decoded JWT Claims: " + decodedJWT.getClaims());

                // 사용자 정보를 attributes에 저장할 수 있습니다.
                attributes.put("user", decodedJWT.getSubject());

                return true; // 토큰이 유효하면 Handshake 진행
            } catch (JWTVerificationException ex) {
                System.err.println("JWT Verification failed: " + ex.getMessage());
            }
        }

        // 토큰이 없거나 유효하지 않으면 Handshake 실패
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        System.out.println("Unauthorized WebSocket handshake request.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Handshake 후 처리 로직 (필요 시 구현)
    }
}
