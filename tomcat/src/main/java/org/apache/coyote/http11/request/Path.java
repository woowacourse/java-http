package org.apache.coyote.http11.request;

public class Path {

    private final String path;

    public Path(String path) {
        this.path = path;
    }

    public String getExtension() {
        String[] split = path.split("\\.");
        if (split.length == 1) {
            return "";
        }
        return split[1].toLowerCase();
    }

    public String get() {
        return path;
    }
}
