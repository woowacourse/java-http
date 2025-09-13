package org.apache.coyote.http11;

import java.util.Arrays;

public enum FileExtension {

    TXT("txt"),
    HTML("html"),
    JAVASCRIPT("js"),
    JSON("json"),
    CSS("css"),
    NONE(),
    ;

    private final String[] extensions;

    FileExtension(String... extensions) {
        this.extensions = extensions;
    }

    public static FileExtension of(final String extension) {
        return Arrays.stream(values())
                .filter(v -> Arrays.stream(v.extensions).anyMatch((ext) -> ext.equalsIgnoreCase(extension)))
                .findFirst()
                .orElse(NONE);
    }

    public String[] getExtensions() {
        return extensions;
    }
}
