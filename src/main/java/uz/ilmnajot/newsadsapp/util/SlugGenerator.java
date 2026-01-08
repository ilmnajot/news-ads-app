package uz.ilmnajot.newsadsapp.util;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import uz.ilmnajot.newsadsapp.repository.CategoryTranslationRepository;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SlugGenerator {

    private final CategoryTranslationRepository translationRepository;

    // Kirill → Lotin transliteratsiya map
    private static final Map<Character, String> CYRILLIC_TO_LATIN = new HashMap<>();

    static {
        // Rus harflari
        CYRILLIC_TO_LATIN.put('а', "a");
        CYRILLIC_TO_LATIN.put('б', "b");
        CYRILLIC_TO_LATIN.put('в', "v");
        CYRILLIC_TO_LATIN.put('г', "g");
        CYRILLIC_TO_LATIN.put('д', "d");
        CYRILLIC_TO_LATIN.put('е', "e");
        CYRILLIC_TO_LATIN.put('ё', "yo");
        CYRILLIC_TO_LATIN.put('ж', "zh");
        CYRILLIC_TO_LATIN.put('з', "z");
        CYRILLIC_TO_LATIN.put('и', "i");
        CYRILLIC_TO_LATIN.put('й', "y");
        CYRILLIC_TO_LATIN.put('к', "k");
        CYRILLIC_TO_LATIN.put('л', "l");
        CYRILLIC_TO_LATIN.put('м', "m");
        CYRILLIC_TO_LATIN.put('н', "n");
        CYRILLIC_TO_LATIN.put('о', "o");
        CYRILLIC_TO_LATIN.put('п', "p");
        CYRILLIC_TO_LATIN.put('р', "r");
        CYRILLIC_TO_LATIN.put('с', "s");
        CYRILLIC_TO_LATIN.put('т', "t");
        CYRILLIC_TO_LATIN.put('у', "u");
        CYRILLIC_TO_LATIN.put('ф', "f");
        CYRILLIC_TO_LATIN.put('х', "h");
        CYRILLIC_TO_LATIN.put('ц', "ts");
        CYRILLIC_TO_LATIN.put('ч', "ch");
        CYRILLIC_TO_LATIN.put('ш', "sh");
        CYRILLIC_TO_LATIN.put('щ', "shch");
        CYRILLIC_TO_LATIN.put('ъ', "");
        CYRILLIC_TO_LATIN.put('ы', "y");
        CYRILLIC_TO_LATIN.put('ь', "");
        CYRILLIC_TO_LATIN.put('э', "e");
        CYRILLIC_TO_LATIN.put('ю', "yu");
        CYRILLIC_TO_LATIN.put('я', "ya");
        
        // O'zbek maxsus harflari
        CYRILLIC_TO_LATIN.put('ў', "o");
        CYRILLIC_TO_LATIN.put('қ', "q");
        CYRILLIC_TO_LATIN.put('ғ', "g");
        CYRILLIC_TO_LATIN.put('ҳ', "h");
    }

    /**
     * Title'dan slug yaratish
     */
    public String generateSlug(String title) {
        if (title == null || title.isEmpty()) {
            return "";
        }

        String slug = title.toLowerCase();

        // Kirill → Lotin
        slug = transliterate(slug);

        // Normalize (NFD) - accents olib tashlash
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", "");

        // Faqat a-z, 0-9, space qoldirish
        slug = slug.replaceAll("[^a-z0-9\\s-]", "");

        // Space'larni tire bilan almashtirish
        slug = slug.trim().replaceAll("\\s+", "-");

        // Ko'p tirelarni bitta qilish
        slug = slug.replaceAll("-+", "-");

        // Boshi va oxiridagi tirelarni olib tashlash
        slug = slug.replaceAll("^-|-$", "");

        return slug;
    }

    /**
     * Unique slug yaratish (agar collision bo'lsa counter qo'shadi)
     */
    public String generateUniqueSlug(String title, String lang, Long categoryId) {
        String baseSlug = generateSlug(title);
        String slug = baseSlug;
        int counter = 1;

        // Bunday slug bormi tekshirish (o'sha tilda va boshqa category'da)
        while (translationRepository.existsBySlugAndLangAndCategoryIdNot(slug, lang, categoryId)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

    /**
     * Kirill harflarni lotin harflarga o'girish
     */
    private String transliterate(String text) {
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            String replacement = CYRILLIC_TO_LATIN.get(c);
            if (replacement != null) {
                result.append(replacement);
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}