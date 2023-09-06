package org.apache.coyote.http11.message.response;

import java.util.Map;
import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.HttpVersion;

public class HttpResponse {

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private Headers headers;
    private ResponseBody responseBody;

    public static HttpResponse init() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHeaders(Headers.fromMap(Map.of()));
        return httpResponse;
    }

    public HttpResponse setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpResponse setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse addHeader(HttpHeaders key, String value) {
        headers.addHeader(key, value);
        return this;
    }

    public HttpResponse setHeaders(Headers headers) {
        this.headers = headers;
        return this;
    }

    public HttpResponse setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
        return this;
    }


    public byte[] getBytes() {
        String statusLine = httpVersion + " " + httpStatus + " ";
        String message = String.join("\r\n", statusLine,
                headers.toString(), "", responseBody.toString());
        return message.getBytes();
    }
}
