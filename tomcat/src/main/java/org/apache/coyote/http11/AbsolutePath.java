package org.apache.coyote.http11;

public class AbsolutePath {

    private static final String DEFAULT_RESOURCE_EXTENSION = ".html";

    private final String value;

    public static AbsolutePath from(String absolutePath) {
        if (!absolutePath.contains(".") && !absolutePath.equals("/")) {
            absolutePath += DEFAULT_RESOURCE_EXTENSION;
        }
        return new AbsolutePath(absolutePath);
    }

    private AbsolutePath(String value) {
        this.value = value;
    }

    public boolean isRootDirectory() {
        return value.equals("/");
    }

    public String value() {
        return value;
    }
}
