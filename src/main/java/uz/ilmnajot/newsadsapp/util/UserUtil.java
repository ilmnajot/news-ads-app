package uz.ilmnajot.newsadsapp.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uz.ilmnajot.newsadsapp.entity.User;
import uz.ilmnajot.newsadsapp.exception.ResourceNotFoundException;
import uz.ilmnajot.newsadsapp.repository.UserRepository;
@RequiredArgsConstructor
@Component
@Slf4j
public class UserUtil {
    private final UserRepository userRepository;
    public User getCurrentUser(){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        log.info("Current user: {}", username);
        log.info("User: {}", this.userRepository.findByUsername(username));
        return this.userRepository.findByUsername(username)
                .orElseThrow(()->new ResourceNotFoundException("User not found"));
    }
}
