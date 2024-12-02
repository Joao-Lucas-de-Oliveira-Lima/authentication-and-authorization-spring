package edu.jl.authenticationandauthorizationspring.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import edu.jl.authenticationandauthorizationspring.dto.security.TokenDto;
import edu.jl.authenticationandauthorizationspring.exception.InvalidJwtAuthenticationException;
import edu.jl.authenticationandauthorizationspring.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {
    @Value("${security.jwt.token.secret}")
    private String secret;
    @Value("${security.jwt.token.expire-length}")
    private Long validityInMilliseconds;
    @Value("${security.jwt.token.refresh-token-multiplier}")
    private int refreshTokenMultiplier;

    private Algorithm algorithm;

    private final UserService userService;

    @Autowired
    public JwtTokenProvider(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
        algorithm = Algorithm.HMAC256(secret.getBytes());
    }

    public TokenDto getAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date expire = new Date(now.getTime() + validityInMilliseconds);
        String accessToken = createAccessToken(username, roles, now, expire);
        String refreshToken = createRefreshToken(username, roles, now);
        return new TokenDto(username, true, now, expire, accessToken, refreshToken);
    }

    private String createRefreshToken(String username, List<String> roles, Date now) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withExpiresAt(new Date(now.getTime() + validityInMilliseconds * refreshTokenMultiplier))
                .withIssuedAt(now)
                .sign(algorithm)
                .strip();
    }

    private String createAccessToken(String username, List<String> roles, Date now, Date expire) {
        String issuerUri = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return JWT.create()
                .withIssuer(issuerUri)
                .withClaim("roles", roles)
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(algorithm)
                .strip();
    }

    public Authentication getAuthentication(String token){
        DecodedJWT decodedJWT = decodedToken(token);
        UserDetails userDetails = userService.loadUserByUsername(decodedJWT.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private DecodedJWT decodedToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        return jwtVerifier.verify(token);
    }

    public String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }

    public Boolean validateToken(String token) throws InvalidJwtAuthenticationException{
        DecodedJWT decodedJWT = decodedToken(token);
        try {
            if(decodedJWT.getExpiresAt().before(new Date())){
                throw new InvalidJwtAuthenticationException("Token expired!");
            }
            return true;
        }catch (Exception e){
            throw new InvalidJwtAuthenticationException("Invalid token!");
        }
    }


}
