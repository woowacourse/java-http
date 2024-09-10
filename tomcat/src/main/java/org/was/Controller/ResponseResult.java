package org.was.Controller;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeader;

public class ResponseResult {

    private final HttpStatusCode statusCode;
    private final ResponseHeader header;
    private final ResponseBody body;

    public ResponseResult(HttpStatusCode statusCode, ResponseHeader header, String body) {
        this.statusCode = statusCode;
        this.header = header;
        this.body = new ResponseBody(body);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public ResponseHeader getHeader() {
        return header;
    }

    public ResponseBody getBody() {
        return body;
    }
}
