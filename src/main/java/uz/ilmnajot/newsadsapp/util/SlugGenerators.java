package uz.ilmnajot.newsadsapp.util;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
@Component
public class SlugGenerators{
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");

    public static String generate(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }

        // Normalize and transliterate
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        
        // Transliteration for Cyrillic (RU) and other characters
        normalized = transliterate(normalized);
        
        // Convert to lowercase
        normalized = normalized.toLowerCase(Locale.ENGLISH);
        
        // Replace whitespace with dashes
        normalized = WHITESPACE.matcher(normalized).replaceAll("-");
        
        // Remove non-latin characters (keep only a-z, 0-9, -)
        normalized = NON_LATIN.matcher(normalized).replaceAll("");
        
        // Replace multiple dashes with single dash
        normalized = MULTIPLE_DASHES.matcher(normalized).replaceAll("-");
        
        // Remove dashes from edges
        normalized = EDGES_DASHES.matcher(normalized).replaceAll("");
        
        return normalized;
    }

    private static String transliterate(String text) {
        // Basic transliteration map for Cyrillic to Latin
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(transliterateChar(c));
        }
        return result.toString();
    }

    private static String transliterateChar(char c) {
        return switch (c) {
            // Russian Cyrillic
            case 'А', 'а' -> "a";
            case 'Б', 'б' -> "b";
            case 'В', 'в' -> "v";
            case 'Г', 'г' -> "g";
            case 'Д', 'д' -> "d";
            case 'Е', 'е' -> "e";
            case 'Ё', 'ё' -> "yo";
            case 'Ж', 'ж' -> "zh";
            case 'З', 'з' -> "z";
            case 'И', 'и' -> "i";
            case 'Й', 'й' -> "y";
            case 'К', 'к' -> "k";
            case 'Л', 'л' -> "l";
            case 'М', 'м' -> "m";
            case 'Н', 'н' -> "n";
            case 'О', 'о' -> "o";
            case 'П', 'п' -> "p";
            case 'Р', 'р' -> "r";
            case 'С', 'с' -> "s";
            case 'Т', 'т' -> "t";
            case 'У', 'у' -> "u";
            case 'Ф', 'ф' -> "f";
            case 'Х', 'х' -> "kh";
            case 'Ц', 'ц' -> "ts";
            case 'Ч', 'ч' -> "ch";
            case 'Ш', 'ш' -> "sh";
            case 'Щ', 'щ' -> "shch";
            case 'Ъ', 'ъ' -> "";
            case 'Ы', 'ы' -> "y";
            case 'Ь', 'ь' -> "";
            case 'Э', 'э' -> "e";
            case 'Ю', 'ю' -> "yu";
            case 'Я', 'я' -> "ya";
            // Uzbek Cyrillic (if used)
            case 'Ў', 'ў' -> "o";
            case 'Қ', 'қ' -> "q";
            case 'Ғ', 'ғ' -> "gh";
            case 'Ҳ', 'ҳ' -> "h";
            default -> String.valueOf(c);
        };
    }

    public static String generateUnique(String base, SlugChecker checker) {
        String slug = generate(base);
        if (slug.isEmpty()) {
            slug = "untitled";
        }
        
        String originalSlug = slug;
        int counter = 1;
        
        while (checker.exists(slug)) {
            slug = originalSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }

    @FunctionalInterface
    public interface SlugChecker {
        boolean exists(String slug);
    }
}

