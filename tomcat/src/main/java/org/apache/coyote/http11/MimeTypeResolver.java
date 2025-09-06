package org.apache.coyote.http11;

import java.util.Map;

public class MimeTypeResolver {

    private static final Map<String, String> mimeTypes = Map.of(
            "html", "text/html; charset=UTF-8",
            "css", "text/css; charset=UTF-8",
            "svg", "image/svg+xml",
            "js", "application/javascript; charset=UTF-8"
    );

    public static String resolve(String ext) {
        return mimeTypes.get(ext);
    }
}
