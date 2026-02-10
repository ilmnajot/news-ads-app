package uz.ilmnajot.newsadsapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    // BadRequestException
    public BadRequestException(String message) {
        super(message);
    }
}
