package nextstep.jwp.resource;

import java.util.Arrays;

public enum FileType {
    PLAIN_TEXT(""),
    HTML("html");

    private String text;

    FileType(String text) {
        this.text = text;
    }

    public static FileType findByName(String extension) {
        return Arrays.stream(values())
            .filter(fileType -> fileType.text.equals(extension))
            .findAny()
            .orElseThrow(() -> new RuntimeException("매치되는 확장자 명이 없습니다."));
    }

    public String getText() {
        return text;
    }
}
