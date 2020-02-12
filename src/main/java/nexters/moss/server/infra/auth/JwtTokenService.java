package nexters.moss.server.infra.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import nexters.moss.server.domain.model.Token;
import nexters.moss.server.domain.model.TokenService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtTokenService implements TokenService {
    private String key;
    private int expirationPeriod;

    // TODO: export to config file
    public JwtTokenService() {
        this.key = "secret";
        this.expirationPeriod = 7;
    }

    @Override
    public String createToken(Long userId, String accessToken) {
        return createToken(userId, accessToken, "");
    }

    @Override
    public String createToken(Long userId, String accessToken, String accountToken) {
        LocalDateTime expirationLocalDateTime = LocalDateTime.now().plus(Duration.ofDays(expirationPeriod));
        Date expirationDate = Date.from(expirationLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(userId.toString())
                .setExpiration(expirationDate)
                .claim("accessToken", accessToken)
                .claim("accountToken", accountToken)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    @Override
    public Token recoverToken(String accountToken) {
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(accountToken);

        Claims body = claims.getBody();

        return new Token(
                Long.parseLong(body.getSubject()),
                body.get("accessToken", String.class),
                body.get("accountToken", String.class)
        );
    }
}
