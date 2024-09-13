package org.apache.coyote.http11.response;

public class ResponseBody {

    private final String value;

    public ResponseBody(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
