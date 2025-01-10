package v1.erpback.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.ClaimsHolder;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import v1.erpback.auth.domain.Jwt;
import v1.erpback.auth.util.JwtTokenVerifier;

import java.util.Date;

@Service
public class JwtTokenService {
    private final JwtTokenVerifier jwtTokenVerifier;
    private int accessTokenExpMinutes = 60 * 24;
    private int refreshTokenExpMinutes = 60 * 24 * 7;

    private JwtTokenService jwtTokenService;

    public static final String SECRET_KEY = "dsdlkfjkadfd123!";

    public JwtTokenService(JwtTokenVerifier jwtTokenVerifier) {
        this.jwtTokenVerifier = jwtTokenVerifier;
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decodedJWT = jwtTokenVerifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            // 로그에 예외 정보 기록
            System.err.println("Invalid JWT token: " + ex.getMessage());
            return false;
        }
    }
    public String createToken(Long id, String name, int expMinutes, String tokenType) {
        Date accessTokenExp = new Date(System.currentTimeMillis() + (60000L * expMinutes));

        String createdToken = JWT.create()
                .withSubject(String.valueOf(id))
                .withClaim("name", name)
                .withClaim("tokenType", tokenType)
                .withExpiresAt(accessTokenExp)
                .sign(Algorithm.HMAC512(SECRET_KEY));
        return createdToken;
    }

    public Jwt createTokens(Long id, String name) {
        String accessToken = createToken(id, name, accessTokenExpMinutes, "accessToken");
        String refreshToken = createToken(id, name, refreshTokenExpMinutes, "refreshToken");

        return Jwt.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExp(new Date(System.currentTimeMillis() + 60000L * accessTokenExpMinutes))
                .refreshTokenExp(new Date(System.currentTimeMillis() + 60000L * refreshTokenExpMinutes))
                .build();
    }

    public Jwt getAccessTokenInfo(String token) {
        String accessToken = null;
        Date accessTokenExp = null;
        Long id = null;

        String receivedToken = token.replace("Bearer ", "").trim();
        try {
            JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(SECRET_KEY);
            DecodedJWT jwt = jwtTokenVerifier.verify(receivedToken);

            if (jwt.getClaim("name") != null) {
                accessToken = receivedToken;
                accessTokenExp = jwt.getExpiresAt();
                id = Long.valueOf(jwt.getSubject());

            } else {
                throw new JWTVerificationException(receivedToken);
            }
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException(receivedToken);
        }
        return Jwt.builder()
                .accessToken(accessToken)
                .refreshToken(receivedToken)
                .accessTokenExp(accessTokenExp)
                .loginId(id)
                .build();
    }

    public Jwt refreshAccessToken(String refreshToken) {
        String accessToken = null;
        JwtTokenVerifier jwtTokenVerifier = new JwtTokenVerifier(SECRET_KEY);
        DecodedJWT decodedJWT = jwtTokenVerifier.verify(refreshToken);

        if (decodedJWT.getClaim("name") != null && decodedJWT.getClaim("tokenType").asString().equals("refreshToken")) {
            accessToken = createToken(Long.valueOf(decodedJWT.getSubject()), decodedJWT.getClaim("name").asString(), accessTokenExpMinutes, "accessToken");
        } else {
            throw new JWTVerificationException(refreshToken);
        }

        return Jwt.builder()
                .accessToken(accessToken)
                .accessTokenExp(new Date(System.currentTimeMillis() + 60000L * accessTokenExpMinutes)).build();
    }



}
