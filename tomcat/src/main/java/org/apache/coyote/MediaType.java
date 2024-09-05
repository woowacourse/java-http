package org.apache.coyote;

import java.util.Arrays;
import java.util.Set;

public enum MediaType {

    APPLICATION_JSON(Type.APPLICATION, SubType.JSON, Set.of("json")),
    TEXT_HTML(Type.TEXT, SubType.HTML, Set.of("html")),
    TEXT_CSS(Type.TEXT, SubType.CSS, Set.of("css")),
    TEXT_PLAIN(Type.TEXT, SubType.PLAIN, Set.of("txt")),
    ALL(Type.ALL, SubType.ALL, Set.of());

    private final Type type;
    private final SubType subType;
    private final Set<String> extensions;

    MediaType(Type type, SubType subType, Set<String> extensions) {
        this.type = type;
        this.subType = subType;
        this.extensions = extensions;
    }

    public String getValue() {
        return type.getValue() + "/" + subType.getValue();
    }

    public static MediaType of(String value) {
        for (MediaType mediaType : values()) {
            if (mediaType.getValue().equals(value)) {
                return mediaType;
            }
        }
        return ALL;
    }

    public static MediaType ofExtension(String extension) {
        return Arrays.stream(values())
                .filter(mediaType -> mediaType.extensions.contains(extension))
                .findFirst()
                .orElse(ALL);
    }

    public enum Type {
        TEXT("text"),
        APPLICATION("application"),
        IMAGE("image"),
        AUDIO("audio"),
        VIDEO("video"),
        ALL("*");

        private final String value;

        Type(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum SubType {
        JSON("json"),
        HTML("html"),
        CSS("css"),
        PLAIN("plain"),
        ALL("*");

        private final String value;

        SubType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
