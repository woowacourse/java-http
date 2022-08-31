package org.apache.coyote.file;

public class DefaultFileHandler extends FileHandler {

    private static final String DEFAULT_PREFIX = "static";

    public DefaultFileHandler() {
        super(DEFAULT_PREFIX);
    }
}
