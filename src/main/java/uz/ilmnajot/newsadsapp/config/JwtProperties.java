package uz.ilmnajot.newsadsapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {
    private String secret;
    private Long accessExpiration = 3600000L; // 1 hour in milliseconds
    private Long refreshExpiration = 2592000000L; // 30 days in milliseconds
}

