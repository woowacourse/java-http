package org.apache.coyote.http11.response;

public class ResponseBody {

    private static final String CRLF = "\r\n";
    private static final String EMPTY = "";

    private final String body;

    public ResponseBody(String body) {
        this.body = body;
    }

    private String convertContentLength() {
        String contentLengthFormat = "Content-Length: %d ";

        return String.format(contentLengthFormat, body.getBytes().length);
    }

    public StringBuilder getBody() {
        if (body.equals(EMPTY)) {
            return new StringBuilder(EMPTY);
        }
        return new StringBuilder()
                .append(convertContentLength()).append(CRLF)
                .append(CRLF)
                .append(body);
    }
}
