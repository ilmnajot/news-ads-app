package uz.ilmnajot.newsadsapp.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.entity.User;

import java.util.UUID;

@Slf4j
@Component
public class UserSession {

    //done
    public UUID getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            log.info("Principal class {} : " , principal.getClass().getName());
            if (principal instanceof User user)
                return user.getId();
        }
        return null;
    }

}
