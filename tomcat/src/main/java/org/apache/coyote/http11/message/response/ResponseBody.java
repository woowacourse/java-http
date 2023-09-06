package org.apache.coyote.http11.message.response;

import org.apache.commons.lang3.StringUtils;

public class ResponseBody {

    private final String content;

    public ResponseBody(String content) {
        this.content = content;
    }

    public static ResponseBody ofEmpty() {
        return new ResponseBody(StringUtils.EMPTY);
    }

    @Override
    public String toString() {
        return content;
    }
}
