package org.apache.coyote;

import java.util.List;
import java.util.stream.Stream;

public enum MimeType {
    TEXT_HTML("text/html;charset=utf-8", "html"),
    TEXT_CSS("text/css;charset=utf-8", "css"),
    TEXT_JAVASCRIPT("text/javascript;charset=utf-8", "js"),
    ;

    private final String typeName;
    private final String expectedExtension;

    MimeType(String typeName, String expectedExtension) {
        this.typeName = typeName;
        this.expectedExtension = expectedExtension;
    }

    public static MimeType determineFromFileName(String extension) {
        return Stream.of(values())
                .filter(mimeType -> mimeType.expectedExtension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 콘텐츠 유형입니다."));
    }

    public String getTypeName() {
        return typeName;
    }
}
