package uz.ilmnajot.newsadsapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategorySlugExistsException extends RuntimeException {
    public CategorySlugExistsException(String message) {
        super(message);
    }
}