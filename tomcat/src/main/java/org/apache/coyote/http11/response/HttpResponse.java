package org.apache.coyote.http11.response;

import java.util.HashMap;
import java.util.StringJoiner;
import org.apache.coyote.http11.common.Constants;
import org.apache.coyote.http11.common.HttpHeader;
import org.apache.coyote.http11.common.HttpStatusCode;

public class HttpResponse {

    private static final double VERSION = 1.1;

    private final ResponseLine responseLine;
    private final HttpHeader headers;
    private ResponseBody body;

    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, new HttpHeader(new HashMap<>()), null);
    }

    public HttpResponse(HttpStatusCode statusCode, HttpHeader headers, byte[] body) {
        this.responseLine = new ResponseLine("HTTP/" + VERSION, statusCode);
        this.headers = headers;
        this.body = new ResponseBody(body);
        headers.headers().put("Content-Length", String.valueOf(this.body.getBody().length));
    }

    public HttpResponse addHeader(String key, String value) {
        headers.headers().put(key, value);
        return this;
    }

    public HttpResponse addCookie(String key, String value) {
        if (headers.headers().containsKey("Set-Cookie")) {
            headers.headers().put("Set-Cookie", headers.headers().get("Set-Cookie") + "; " + key + "=" + value);
            return this;
        }
        headers.headers().put("Set-Cookie", key + "=" + value);
        return this;
    }

    public HttpResponse setBody(String body) {
        this.body = new ResponseBody(body.getBytes());
        headers.headers().put("Content-Length", String.valueOf(this.body.size()));
        return this;
    }

    public HttpResponse setStatus(HttpStatusCode statusCode) {
        responseLine.setStatusCode(statusCode);
        return this;
    }

    public HttpResponse sendRedirect(String location) {
        setStatus(HttpStatusCode.FOUND);
        addHeader("Location", location);
        return this;
    }

    public String toHttpMessage() {
        StringJoiner joiner = new StringJoiner(Constants.CRLF);
        joiner.add(responseLine.toResponseString());
        joiner.add(headers.toString());
        joiner.add("");
        joiner.add(new String(body.getBody()));
        return joiner.toString();
    }

    public byte[] getAsBytes() {
        return toHttpMessage().getBytes();
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "responseLine=" + responseLine +
                ", header=" + headers +
                ", body=" + body +
                '}';
    }
}
