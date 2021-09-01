package nextstep.jwp.http.response;

import java.util.Set;

public class ResponseHeader {

    private final ResponseHeaders headers;

    private ResponseLine line;

    public ResponseHeader() {
        this(new ResponseHeaders());
    }

    private ResponseHeader(ResponseHeaders headers) {
        this.headers = headers;
    }

    public void setContentType(String contentType) {
        headers.setContentType(contentType);
    }

    public void setContentLength(int contentLength) {
        headers.setContentLength(contentLength);
    }

    public void setLine(HttpStatus httpStatus) {
        this.line = new ResponseLine(httpStatus);
    }

    public Set<String> getKeySet() {
        return headers.getKeySet();
    }

    public String getValue(String key) {
        return headers.getValue(key);
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }

    public ResponseLine getLine() {
        return line;
    }
}
