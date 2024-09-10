package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ContentType {

    TEXT_HTML("text", "html"),
    TEXT_CSS("text", "css"),
    TEXT_JS("text", "js"),
    APPLICATION_JSON("application", "json"),
    IMAGE_ICON("image", "ico");

    private static final Map<String, ContentType> SUIT_CASE = Arrays.stream(ContentType.values())
            .collect(Collectors.toMap(ContentType::getValue, Function.identity()));

    private final String category;
    private final String value;

    ContentType(final String category, final String value) {
        this.category = category;
        this.value = value;
    }

    public static ContentType from(final Path path) {
        final var absolutePath = path.getAbsolutePath().getPath();
        final var extension = absolutePath.substring(absolutePath.lastIndexOf(".") + 1);
        if (SUIT_CASE.containsKey(extension)) {
            return SUIT_CASE.get(extension);
        }
        throw new IllegalArgumentException(extension + " 는 지원하지 않는 형식입니다.");
    }

    public String getResponseText() {
        return "Content-Type: " + category + "/" + value;
    }

    public String getResponseText(final Charset charset) {
        return "Content-Type: " + category + "/" + value + "; charset=" + charset.name;
    }

    public String getValue() {
        return value;
    }

    public enum Charset {
        ISO_LATIN_1("iso-latin-1"),
        UTF_8("utf-8");

        private final String name;

        Charset(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
