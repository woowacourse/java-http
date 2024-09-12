package org.apache.coyote.http11.request;

public class RequestParam {

    private static final String DELIMITER_OF_KEY_VALUE = "=";

    private final String key;
    private final String value;

    public RequestParam(String rawRequestParam) {
        String[] tokens = rawRequestParam.split(DELIMITER_OF_KEY_VALUE);
        this.key = tokens[0];
        this.value = tokens[1];
    }
}
