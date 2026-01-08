package uz.ilmnajot.newsadsapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Date accessTokenExpireDate;
    private Date refreshTokenExpireDate;
}

