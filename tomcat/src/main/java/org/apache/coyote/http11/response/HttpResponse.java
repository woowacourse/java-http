package org.apache.coyote.http11.response;

import java.io.IOException;
import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";

    private final String responseLine;
    private final ResponseBody body;
    private final ResponseHeaders headers;


    public HttpResponse(String responseLine, ResponseBody body, ResponseHeaders header) {
        this.responseLine = responseLine;
        this.body = body;
        this.headers = header;
    }

    public static HttpResponse createWithBody(HttpStatus httpStatus, String url) throws IOException {
        final String responseLine = HTTP_VERSION + httpStatus.code;
        final ResponseBody body = ResponseBody.from(url);
        final ResponseHeaders header = new ResponseHeaders(url, body.getBody());
        return new HttpResponse(responseLine, body, header);
    }

    public static HttpResponse createWithoutBody(HttpStatus httpStatus, String redirectUrl) {
        final String responseLine = HTTP_VERSION + httpStatus.code;
        final ResponseBody body = new ResponseBody();
        final ResponseHeaders header = new ResponseHeaders(redirectUrl);
        return new HttpResponse(responseLine, body, header);
    }

    public byte[] getBytes() {
        String response = String.join("\r\n", responseLine, headers.getHeadersToString(), body.getBody());
        return response.getBytes();
    }

    public String getResponseLine() {
        return responseLine;
    }

    public ResponseBody getBody() {
        return body;
    }

    public ResponseHeaders getHeader() {
        return headers;
    }
}
