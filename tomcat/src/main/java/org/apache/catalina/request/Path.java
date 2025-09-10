package org.apache.catalina.request;

public class Path {

    private static final char PATH_DELIMITER = '?';

    private final String value;

    public Path(String uri) {
        final int queryIndex = uri.indexOf(PATH_DELIMITER);

        if (queryIndex == -1) {
            this.value = uri;
            return;
        }

        this.value = uri.substring(0, queryIndex);
    }

    public String getValue() {
        return value;
    }
}
