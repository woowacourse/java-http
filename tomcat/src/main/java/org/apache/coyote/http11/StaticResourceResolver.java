package org.apache.coyote.http11;

import java.net.URL;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequestHeader;

public final class StaticResourceResolver {
    private static final String STATIC_ROOT = "static";

    private static final String MIME_TYPE_DEFAULT = "text/html";
    private static final String MIME_TYPES_WILDCARD = "*/*";
    private static final Map<String, String> MIME_TYPE = Map.ofEntries(
            Map.entry("text/html", ".html"),
            Map.entry("text/css", ".css"),
            Map.entry("text/javascript", ".js")
    );

    private StaticResourceResolver(){
    }

    private String resolveContentType(HttpRequestHeader header) {
        String accept = header.header().get("Accept");

        if (accept == null || accept.isEmpty()) {
            return MIME_TYPE_DEFAULT;
        }

        String preferred = accept.split(",")[0].split(";")[0].trim();

        if (MIME_TYPES_WILDCARD.equals(preferred)) {
            return MIME_TYPE_DEFAULT;
        }

        return preferred;
    }

    public static URL findStaticResource(String path, String contentType) {
        if (path.contains(".")) {
            return StaticResourceResolver.class.getClassLoader().getResource(STATIC_ROOT + path);
        }

        return StaticResourceResolver.class.getClassLoader().getResource(STATIC_ROOT + path + MIME_TYPE.get(contentType));
    }
}
