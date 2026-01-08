package uz.ilmnajot.newsadsapp.security;

import com.fasterxml.jackson.databind.node.LongNode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.access.expiration}")
    private Long accessExpirationTime;

    @Value("${app.refresh.expiration}")
    private Long refreshExpirationTime;

    //done
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(this.secret.getBytes(StandardCharsets.UTF_8));
    }

    //done
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    //done
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    //done

    //done
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //done
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //done
    private Boolean isTokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    //done
    public String generateAccessToken(UserDetails userDetails) {
        return this.createToken(
                new HashMap<>(),
                userDetails.getUsername(),
                this.accessExpirationTime);
    }

    //done
    public String generateRefreshToken(UserDetails userDetails) {
        return this.createToken(
                new HashMap<>(),
                userDetails.getUsername(),
               this.refreshExpirationTime);
    }

    //done
    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    //done
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = this.getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}

