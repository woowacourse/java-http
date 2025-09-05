package org.apache.coyote.http11;

import java.util.Map;

public class ContentTypeMapper {

    private static final Map<String, String> TYPES = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css",
            "js", "application/javascript"
    );

    public static String get(String extension) {
        int dotIndex = extension.lastIndexOf('.');
        if (dotIndex == -1) {
            return "application/octet-stream";
        }
        String ext = extension.substring(dotIndex + 1);
        return TYPES.getOrDefault(ext, "application/octet-stream");
    }
}
