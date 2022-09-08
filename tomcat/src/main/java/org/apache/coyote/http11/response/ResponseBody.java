package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.util.StringUtils.EMPTY;

public class ResponseBody {

    private static final ResponseBody NONE = new ResponseBody(EMPTY);

    public static ResponseBody None() {
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
