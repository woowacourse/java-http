package org.apache.catalina.util;

import java.net.URL;
import java.util.Map;

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

    public static URL findStaticResource(String path, String contentType) {
        if (path.contains(".")) {
            return StaticResourceResolver.class.getClassLoader().getResource(STATIC_ROOT + path);
        }

        return StaticResourceResolver.class.getClassLoader().getResource(STATIC_ROOT + path + MIME_TYPE.get(contentType));
    }
}
