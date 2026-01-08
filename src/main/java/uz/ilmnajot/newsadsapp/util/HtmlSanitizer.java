package uz.ilmnajot.newsadsapp.util;

import com.google.common.html.HtmlEscapers;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

public class HtmlSanitizer {
    
    // Allowed HTML tags for news content
    private static final Safelist CONTENT_SAFELIST = Safelist.relaxed()
            .addTags("h1", "h2", "h3", "h4", "h5", "h6", "p", "br", "strong", "em", "u", "ul", "ol", "li", "blockquote")
            .addAttributes("a", "href", "title", "target")
            .addAttributes("img", "src", "alt", "title", "width", "height")
            .addProtocols("a", "href", "http", "https", "mailto")
            .addProtocols("img", "src", "http", "https")
            .preserveRelativeLinks(true);

    /**
     * Sanitize HTML content to prevent XSS attacks
     * Allows safe HTML tags and attributes
     */
    public static String sanitize(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        return Jsoup.clean(html, CONTENT_SAFELIST);
    }

    /**
     * Escape HTML entities (for plain text that should be displayed as text)
     */
    public static String escape(String text) {
        if (text == null) {
            return "";
        }
        return HtmlEscapers.htmlEscaper().escape(text);
    }

    /**
     * Strip all HTML tags, keeping only text content
     */
    public static String stripHtml(String html) {
        if (html == null || html.trim().isEmpty()) {
            return "";
        }
        return Jsoup.parse(html).text();
    }
}

