package org.apache.coyote.http11.response;

public class ResponseBody {

    public static final ResponseBody EMPTY_RESPONSE_BODY = new ResponseBody("");

    private final String value;

    public ResponseBody(final String value) {
        this.value = value;
    }

    public String toMessage() {
        return String.format("%n%s", value);
    }

    public String getValue() {
        return value;
    }
}
