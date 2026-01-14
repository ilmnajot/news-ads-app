package uz.ilmnajot.newsadsapp.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limit annotation for request throttling
 *
 * Usage:
 * @RateLimit(limit = 5, duration = 1, timeUnit = TimeUnit.MINUTES)
 */
@Target(ElementType.METHOD)  // Faqat method'larda ishlatiladi
@Retention(RetentionPolicy.RUNTIME)  // Runtime'da o'qiladi
public @interface RateLimit {

    /**
     * Maksimal so'rovlar soni
     */
    int limit() default 10;

    /**
     * Vaqt oynasi
     */
    int duration() default 1;

    /**
     * Vaqt birligi (SECONDS, MINUTES, HOURS)
     */
    TimeUnit timeUnit() default TimeUnit.MINUTES;

    /**
     * Xato xabari
     */
    String message() default "Too many requests. Please try again later.";
}