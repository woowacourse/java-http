package org.apache.coyote.http11.common.request;

public class RequestUri {

    public static final int START_OF_LINE = 0;
    public static final String START_MARK_OF_QUERY_PARAMS = "?";
    public static final int HAS_NO_QUERY_PARAMS = -1;
    public static final String START_MARK_OF_EXTENSION = ".";
    public static final int HAS_NO_EXTENSION = -1;

    private final String value;

    private RequestUri(final String value) {
        this.value = value;
    }

    public static RequestUri create(String line) {
        int idx = line.indexOf(START_MARK_OF_QUERY_PARAMS);
        if (idx == HAS_NO_QUERY_PARAMS) {
            return new RequestUri(line);
        }
        return new RequestUri(line.substring(START_OF_LINE, idx));
    }

    public String getExtension() {
        int idx = value.indexOf(START_MARK_OF_EXTENSION);
        if (idx == HAS_NO_EXTENSION) {
            return "";
        }
        return value.substring(idx);
    }

    public String getValue() {
        return value;
    }
}
