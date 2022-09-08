package org.apache.coyote.response;

import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import org.apache.catalina.cookie.HttpCookie;
import org.apache.catalina.cookie.HttpCookies;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpStatus;

public class HttpResponse {

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    private final ResponseHeaders headers = new ResponseHeaders(new HashMap<>());
    private final HttpCookies cookies = new HttpCookies(new HashMap<>());
    private final View view = new View();
    private String messageBody = "";

    public HttpResponse status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse ok() {
        return status(HttpStatus.OK);
    }

    public HttpResponse redirect(String location) {
        this.headers.addHeader(HttpHeader.LOCATION, location);
        return status(HttpStatus.FOUND);
    }

    public HttpResponse setViewResource(String viewResource) {
        final var path = view.toResourcePath(viewResource);
        this.headers.addHeader(HttpHeader.CONTENT_TYPE, view.findContentType(path));
        return setMessageBody(view.findContent(path));
    }

    public HttpResponse addSetCookieHeader(String name, HttpCookie value) {
        cookies.setCookie(name, value);
        return this;
    }

    private HttpResponse setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        String contentLength = String.valueOf(messageBody.getBytes().length);
        this.headers.addHeader(HttpHeader.CONTENT_LENGTH, contentLength);
        return this;
    }

    public String toMessage() {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(status.toStatusLine());
        for (String header : toHeaderLines()) {
            joiner.add(header);
        }
        joiner.add("");
        if (!messageBody.isBlank()) {
            joiner.add(messageBody);
        }
        return joiner.toString();
    }

    private List<String> toHeaderLines() {
        List<String> headers = this.headers.toMessageLines();
        if (cookies.containsCookies()) {
            headers.addAll(cookies.toSetHeaderFormats());
        }
        return headers;
    }
}
