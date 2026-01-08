package uz.ilmnajot.newsadsapp.config;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder();
    }

    private static class Argon2PasswordEncoder implements PasswordEncoder {
        private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

        @Override
        public String encode(CharSequence rawPassword) {
            return argon2.hash(10, 65536, 1, rawPassword.toString().toCharArray());
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return argon2.verify(encodedPassword, rawPassword.toString().toCharArray());
        }
    }
}

