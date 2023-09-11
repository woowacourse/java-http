package common.http;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js"),
    ICO("image/ico", "ico"),
    ;

    public static final String DELIMITER_FOR_EXTENSION = ".";

    private final String type;
    private final String extension;

    ContentType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ContentType findByExtension(String extension) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 확장자명 입니다."));
    }

    public static ContentType findByPath(String path) {
        int indexBeforeExtension = path.lastIndexOf(DELIMITER_FOR_EXTENSION);
        if (indexBeforeExtension == -1) {
            throw new IllegalArgumentException("파일의 확장자명이 없습니다.");
        }

        String extension = path.substring(indexBeforeExtension + 1);
        return findByExtension(extension);
    }

    public String getType() {
        return type;
    }
}
