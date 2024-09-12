package org.apache.coyote.http11.request;

public class RequestParam {

    private static final String DELIMITER_OF_KEY_VALUE = "=";
    private static final int INDEX_OF_KEY = 0;
    private static final int INDEX_OF_VALUE = 1;

    private final String key;
    private final String value;

    public RequestParam(String rawRequestParam) {
        String[] tokens = rawRequestParam.split(DELIMITER_OF_KEY_VALUE);
        this.key = tokens[INDEX_OF_KEY];
        this.value = tokens[INDEX_OF_VALUE];
    }
}
