package nextstep.jwp.resource;

import static java.util.stream.Collectors.toList;
import static nextstep.jwp.http.MimeType.ALL;
import static nextstep.jwp.http.MimeType.TEST_HTML;
import static nextstep.jwp.http.MimeType.TEXT_CSS;
import static nextstep.jwp.http.MimeType.TEXT_JS;
import static nextstep.jwp.http.MimeType.TEXT_PLAIN;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.http.MimeType;

public enum FileType {
    PLAIN_TEXT("txt", List.of(TEXT_PLAIN, ALL)),
    HTML("html", List.of(TEST_HTML, ALL)),
    CSS("css", List.of(TEXT_CSS, ALL)),
    JS("js", List.of(TEXT_JS, ALL)),
    ICO("ico", List.of(ALL)),
    SVG("svg", List.of(ALL));

    private static final List<FileType> CACHE_LIST = Arrays.stream(values())
        .collect(toList());
    private final String text;
    private final List<MimeType> mimeTypes;

    FileType(String text, List<MimeType> mimeTypes) {
        this.text = text;
        this.mimeTypes = mimeTypes;
    }

    public static FileType findByName(String extension) {
        return CACHE_LIST.stream()
            .filter(fileType -> fileType.text.equals(extension))
            .findAny()
            .orElseThrow(() -> new RuntimeException("매치되는 확장자 명이 없습니다."));
    }

    public static List<FileType> findByMimeType(MimeType mimeType) {
        return CACHE_LIST.stream()
            .filter(fileType -> fileType.mimeTypes.contains(mimeType))
            .collect(toList());
    }

    public String getText() {
        return text;
    }
}
