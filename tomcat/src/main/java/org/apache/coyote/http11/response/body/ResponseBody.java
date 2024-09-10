package org.apache.coyote.http11.response.body;

public class ResponseBody {

    private static final String EMPTY_BODY = "";

    private final String responseBody;

    public ResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public static ResponseBody empty() {
        return new ResponseBody(EMPTY_BODY);
    }

    public String write() {
        return responseBody;
    }
}
