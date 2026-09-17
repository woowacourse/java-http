package org.apache.coyote;

import java.util.List;
import java.util.stream.Stream;

public enum MimeType {
    TEXT_HTML("text/html;charset=utf-8", List.of("html", "htm")),
    TEXT_CSS("text/css;charset=utf-8", List.of("css")),
    TEXT_JAVASCRIPT("text/javascript;charset=utf-8", List.of("js")),
    ;

    private final String typeName;
    private final List<String> expectedExtensions;

    MimeType(String typeName, List<String> expectedExtensions) {
        this.typeName = typeName;
        this.expectedExtensions = expectedExtensions;
    }

    public static MimeType determineFromFileName(String extension) {
        return Stream.of(values())
                .filter(mimeType -> mimeType.expectedExtensions.contains(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 콘텐츠 유형입니다."));
    }

    public String getTypeName() {
        return typeName;
    }
}
