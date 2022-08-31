package org.apache.coyote.common;

import java.util.Arrays;

public enum FileExtension {

    HTML(".html"),
    CSS(".css"),
    NONE("");
    ;

    private final String value;

    FileExtension(final String value) {
        this.value = value;
    }

    public static FileExtension of(final String resourceInformation) {
        return Arrays.stream(values())
                .filter(fileExtension -> resourceInformation.contains(fileExtension.value))
                .findFirst()
                .orElse(NONE);
    }
}
