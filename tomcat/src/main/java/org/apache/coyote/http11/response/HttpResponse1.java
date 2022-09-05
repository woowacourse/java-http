package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse1 {

    private final Body body;
    private final Header header;

    private HttpResponse1(Body body, Header header) {
        this.body = body;
        this.header = header;
    }

    public static HttpResponse1 of(HttpStatus httpStatus, String url) throws IOException {
        final Body body = Body.from(url);
        final Header header = new Header(httpStatus, url, body.getBody());
        return new HttpResponse1(body, header);
    }

    public static HttpResponse1 from(HttpStatus httpStatus, String redirectUrl) {
        final Body body = new Body();
        final Header header = new Header(httpStatus, redirectUrl);
        return new HttpResponse1(body, header);
    }

    public byte[] getBytes() {
        String response = String.join("\r\n", header.getHeader(), body.getBody(), "");
        return response.getBytes();
    }
}
