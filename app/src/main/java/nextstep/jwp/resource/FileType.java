package nextstep.jwp.resource;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public enum FileType {
    PLAIN_TEXT("txt"),
    HTML("html"),
    CSS("css"),
    JS("js"),
    ICO("ico"),
    SVG("svg"),
    NONE("");

    private static final Map<String, FileType> CACHE = Arrays.stream(values())
        .collect(toMap(FileType::getText,Function.identity()));

    private final String text;

    FileType(String text) {
        this.text = text;
    }

    public static FileType findByName(String extension) {
        return CACHE.getOrDefault(extension, NONE);
    }

    public String getText() {
        return text;
    }
}
