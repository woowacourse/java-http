package nextstep.jwp.http.response;

import nextstep.jwp.http.ResponseHeaders;

public class HttpResponse {

    private HttpStatus httpStatus;
    private final ResponseHeaders headers;
    private String body;

    public HttpResponse(ResponseHeaders headers) {
        this.headers = headers;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void addHeaders(String key, String value) {
        headers.addHeaders(key, value);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ResponseHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
