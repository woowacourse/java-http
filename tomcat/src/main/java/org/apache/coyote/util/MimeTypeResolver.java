package org.apache.coyote.util;

import java.util.Map;

public class MimeTypeResolver {

    private static final Map<String, String> availableMimeTypes = Map.of(
            "html", "text/html;charset=utf-8",
            "css", "text/css;charset=utf-8"
    );

    public static String resolve(String path) {
        int dotIndex = path.lastIndexOf(".");
        if (dotIndex == -1) {
            // 처리할 수 없는 MIME 타입인 경우, 기본(fallback)을 application/octet-stream으로 처리한다.
            // 참고: https://developer.mozilla.org/ko/docs/Web/HTTP/Guides/MIME_types/Common_types
            return "application/octet-stream";
        }

        // 확장자 추출
        String ext = path.substring(dotIndex + 1);

        return availableMimeTypes.getOrDefault(ext, "application/octet-stream");
    }
}
