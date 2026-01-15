package uz.ilmnajot.newsadsapp.config;

import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import uz.ilmnajot.newsadsapp.util.UserSession;

import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableJpaAuditing(auditorAwareRef = "jpaAuditingConfig")
public class JpaAuditingConfig implements AuditorAware<Long> {

    private final UserSession userSession;
//
//    @PostConstruct
//    public void init() {
//        log.info("Initializing Hibernate successfully");
//    }

    @Nonnull
    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId = userSession.getUser();
        log.info("Current auditor: {}", userId);
        return Optional.ofNullable(userId);
    }
}

