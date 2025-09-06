package org.apache.coyote.http11;

import java.util.Map;

public class MimeTypeResolver {

    private static final Map<String, String> mimeTypes = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css;charset=utf-8",
            "svg", "image/svg+xml",
            "js", "application/javascript;charset=utf-8"
    );

    public static String resolve(String ext) {
        return mimeTypes.get(ext);
    }
}
