package org.apache.coyote.http11.message.response;

import org.apache.commons.lang3.StringUtils;

public class ResponseBody {

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public static ResponseBody ofEmpty() {
        return new ResponseBody(StringUtils.EMPTY);
    }

    @Override
    public String toString() {
        return body;
    }
}
