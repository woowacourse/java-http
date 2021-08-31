package nextstep.jwp.resource;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

public enum FileType {
    PLAIN_TEXT("txt"),
    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico"),
    SVG("svg"),
    NONE("");

    private static final List<FileType> CACHE_LIST = Arrays.stream(values())
        .collect(toList());
    private final String text;

    FileType(String text) {
        this.text = text;
    }

    public static FileType findByName(String extension) {
        return CACHE_LIST.stream()
            .filter(fileType -> fileType.text.equals(extension))
            .findAny()
            .orElseThrow(() -> new RuntimeException("매치되는 확장자 명이 없습니다."));
    }

    public String getText() {
        return text;
    }
}
