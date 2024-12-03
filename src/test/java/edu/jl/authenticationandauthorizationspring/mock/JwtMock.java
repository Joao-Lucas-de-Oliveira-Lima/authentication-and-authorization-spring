package edu.jl.authenticationandauthorizationspring.mock;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

public abstract class JwtMock extends UserMock{
    @Value("${security.jwt.token.expire-length}")
    private Long tokenValidityInMilliseconds;

    @Value("${security.jwt.token.secret}")
    private String jwtSecret;

    private final String invalidJwtSecret = "invalid_secret";

    private Algorithm validJwtAlgorithm;
    private Algorithm invalidJwtAlgorithm;

    @PostConstruct
    private void initializeAlgorithms() {
        String encodedJwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
        String encodedInvalidJwtSecret = Base64.getEncoder().encodeToString(invalidJwtSecret.getBytes());

        validJwtAlgorithm = Algorithm.HMAC512(encodedJwtSecret.getBytes());
        invalidJwtAlgorithm = Algorithm.HMAC512(encodedInvalidJwtSecret.getBytes());
    }

    public String generateValidToken(String username, List<String> roles) {
        return createToken(username, roles, tokenValidityInMilliseconds, validJwtAlgorithm);
    }

    public String generateInvalidToken(String username, List<String> roles) {
        return createToken(username, roles, tokenValidityInMilliseconds, invalidJwtAlgorithm);
    }


    public String generateTokenWithOneSecondValidity(String username, List<String> roles) {
        return createToken(username, roles, 1000L, validJwtAlgorithm);
    }

    private String createToken(String username, List<String> roles, Long validityDuration, Algorithm signingAlgorithm) {
        String issuer = "Test";
        Date now = new Date();
        Date expiration = new Date(now.getTime() + validityDuration);

        return JWT.create()
                .withSubject(username)
                .withIssuer(issuer)
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(signingAlgorithm);
    }
}
