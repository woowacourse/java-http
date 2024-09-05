package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum FileExtension {
    CSS("css"),
    HTML("html"),
    JAVASCRIPT("js"),
    UNKNOWN("");

    private final String extension;

    private static final Map<String, FileExtension> CLASSIFY =
            Arrays.stream(FileExtension.values())
                    .collect(Collectors.toMap(FileExtension::getExtension, Function.identity()));

    FileExtension(final String extension) {
        this.extension = extension;
    }

    public static FileExtension from(final String extension) {
        return Optional.ofNullable(CLASSIFY.get(extension.toLowerCase()))
                .orElse(UNKNOWN);
    }

    public String getExtension() {
        return extension;
    }
}
