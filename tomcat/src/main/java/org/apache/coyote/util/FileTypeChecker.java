package org.apache.coyote.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileTypeChecker {

    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(List.of(".html", ".css", ".js"));

    private FileTypeChecker() {
    }

    public static boolean isSupported(String target) {
        if (SUPPORTED_EXTENSIONS.stream().noneMatch(target::endsWith)) {
            throw new IllegalArgumentException(target + "은(는) 지원하지 않는 파일 형식입니다.");
        }
        return true;
    }

    public static boolean isHtml(String contentType) {
        return contentType.equals("text/html");
    }
}
