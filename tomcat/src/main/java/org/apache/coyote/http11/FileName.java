package org.apache.coyote.http11;

import java.util.Objects;

public class FileName {

    private static final String EXTENSION_DELIMITER = ".";
    private final String prefix;
    private final String extension;

    public FileName(String prefix, String extension) {
        this.prefix = prefix;
        this.extension = extension;
    }

    public String concat() {
        return prefix + EXTENSION_DELIMITER + extension;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getExtension() {
        return extension;
    }
}
