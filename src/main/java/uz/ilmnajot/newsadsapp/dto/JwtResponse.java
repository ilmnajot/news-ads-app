package uz.ilmnajot.newsadsapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.ilmnajot.newsadsapp.security.SecurityConstants;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = SecurityConstants.BEARER_PREFIX;
    private Date accessTokenExpireDate;
    private Date refreshTokenExpireDate;
}

