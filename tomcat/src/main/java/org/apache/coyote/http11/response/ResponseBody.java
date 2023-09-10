package org.apache.coyote.http11.response;

public class ResponseBody {

    private static final String CRLF = "\r\n";
    private static final String EMPTY = "";

    public static final ResponseBody RESPONSE_BODY = new ResponseBody(EMPTY);

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    public static ResponseBody empty() {
        return RESPONSE_BODY;
    }

    public StringBuilder getBody() {
        if (body.equals(EMPTY)) {
            return new StringBuilder(EMPTY);
        }
        return new StringBuilder()
                .append(CRLF)
                .append(body);
    }
}
