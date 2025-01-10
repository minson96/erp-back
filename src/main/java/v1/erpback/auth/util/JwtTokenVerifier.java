package v1.erpback.auth.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenVerifier {
    private final JWTVerifier jwtVerifier;

    public JwtTokenVerifier(@Value("${jwt.secret}") String secret) {
        Algorithm algorithm = Algorithm.HMAC512(secret);
        jwtVerifier = JWT.require(algorithm).build();
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        return jwtVerifier.verify(token);
    }
}
