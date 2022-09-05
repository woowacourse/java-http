package org.apache.coyote.http11.response;

public class ResponseBody {

    private static final String EMPTY = "";
    private static final ResponseBody NONE = new ResponseBody(EMPTY);

    public static ResponseBody NONE() {
        return NONE;
    }

    private final String value;

    public ResponseBody(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
