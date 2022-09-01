package org.apache.coyote.http11;

import java.util.Objects;

public class FileName {

    private final String prefix;
    private final String extension;

    public FileName(String prefix, String extension) {
        this.prefix = prefix;
        this.extension = extension;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getExtension() {
        return extension;
    }
}
