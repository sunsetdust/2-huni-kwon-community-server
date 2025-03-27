package community.startupcode.security;

import community.startupcode.Model.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
@Service
public class JWTProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    // JWT 토큰 생성 로직
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(Long.toString(user.getId()))
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // 토큰에서 사용자 아이디 추출
    public Long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // 토큰 유효성 검사
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            // 잘못된 JWT 서명
        } catch (MalformedJwtException ex) {
            // JWT 토큰이 잘못됨
        } catch (ExpiredJwtException ex) {
            // JWT 토큰 만료됨
        } catch (UnsupportedJwtException ex) {
            // 지원하지 않는 JWT 토큰
        } catch (IllegalArgumentException ex) {
            // JWT 토큰이 비어 있음
        }
        return false;
    }
}
