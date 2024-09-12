package org.apache.catalina.view;

import java.util.Map;

class ContentTypeConverter {

    private static final Map<String, String> MAP = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript"
    );

    String mapToContentType(String fileExtension) {
        if (MAP.get(fileExtension) == null) {
            return "text/html";
        }

        return MAP.get(fileExtension);
    }
}
