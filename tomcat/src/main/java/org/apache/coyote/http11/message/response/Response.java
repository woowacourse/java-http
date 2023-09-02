package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.Headers;
import org.apache.coyote.http11.message.HttpStatus;
import org.apache.coyote.http11.message.HttpVersion;

public class Response {

    private final String statusLine;
    private final Headers headers;
    private final ResponseBody body;

    private Response(String statusLine, Headers headers, ResponseBody body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static Response from(HttpVersion httpVersion, HttpStatus httpStatus,
                                Headers headers, ResponseBody responseBody) {
        String statusLine = httpVersion + " " + httpStatus + " ";
        return new Response(statusLine, headers, responseBody);
    }

    public byte[] getBytes() {
        String message = String.join("\r\n", statusLine,
                headers.toString(), "", body.toString());
        return message.getBytes();
    }
}
