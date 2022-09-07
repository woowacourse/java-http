package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final Body body;
    private final ResponseHeaders header;

    private HttpResponse(Body body, ResponseHeaders header) {
        this.body = body;
        this.header = header;
    }

    public static HttpResponse createWithBody(HttpStatus httpStatus, String url) throws IOException {
        final Body body = Body.from(url);
        final ResponseHeaders header = new ResponseHeaders(httpStatus, url, body.getBody());
        return new HttpResponse(body, header);
    }

    public static HttpResponse createWithoutBody(HttpStatus httpStatus, String redirectUrl) {
        final Body body = new Body();
        final ResponseHeaders header = new ResponseHeaders(httpStatus, redirectUrl);
        return new HttpResponse(body, header);
    }

    public byte[] getBytes() {
        String response = String.join("\r\n", header.getHeader(), body.getBody());
        return response.getBytes();
    }

    public Body getBody() {
        return body;
    }

    public ResponseHeaders getHeader() {
        return header;
    }
}
