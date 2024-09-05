package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Set;

/**
 * Enumerates content types. see <a href=https://datatracker.ietf.org/doc/html/rfc2616#section-3.7>RFC 2616, section 3.7</a>
 */
public enum ContentType {

    TEXT_HTML("text/html;", Set.of("html", "htm")),
    TEXT_CSS("text/css", Set.of("css")),
    TEXT_JAVASCRIPT("text/javascript", Set.of("js")),
    APPLICATION_JSON("application/json", Set.of("json")),
    ;

    private final String mediaType;
    private final Set<String> extensions;

    ContentType(String mediaType, Set<String> extensions) {
        this.mediaType = mediaType;
        this.extensions = extensions;
    }

    public static ContentType fromExtension(String extension) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.supportsExtension(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported extension: " + extension));
    }

    private boolean supportsExtension(String extension) {
        return extensions.contains(extension);
    }

    public String getMediaType() {
        return mediaType;
    }
}
