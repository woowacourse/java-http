package org.apache.coyote.http11.file;

import java.util.Arrays;

public enum ResourceExtension {

    HTML(".html"),
    CSS(".css"),
    JAVASCRIPT(".js"),
    ICO(".ico");

    private final String extension;

    ResourceExtension(final String extension) {
        this.extension = extension;
    }

    public static boolean contains(final String extension) {
        return Arrays.stream(values())
                .anyMatch(it -> extension.endsWith(it.extension));
    }
}
