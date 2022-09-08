package org.apache.coyote.support;

import java.io.IOException;
import org.apache.coyote.exception.FileExtensionNotSupportedException;
import org.apache.coyote.exception.InvalidUrlException;
import org.apache.coyote.file.StaticFileHandler;

public class Url {

    private static final String DEFAULT_PATH = "/";

    private final String value;

    public Url(final String value) {
        this.value = value;
    }

    public static Url createUrl(final String url) {
        if (!url.contains("/")) {
            throw new InvalidUrlException();
        }
        return new Url(url);
    }

    public boolean isDefaultPath() {
        return value.equals(DEFAULT_PATH);
    }

    public String extractFileExtension() {
        int index = value.lastIndexOf(".");
        if (index == -1) {
            throw new FileExtensionNotSupportedException();
        }
        return value.substring(index + 1);
    }

    public String extractFileLines() throws IOException {
        return StaticFileHandler.getFileLines(value);
    }

    public String getValue() {
        return value;
    }
}
