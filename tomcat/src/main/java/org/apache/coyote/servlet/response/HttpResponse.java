package org.apache.coyote.servlet.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.cookie.HttpCookies;
import org.apache.coyote.support.HttpStatus;

public class HttpResponse {

    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    private String location;
    private final HttpCookies cookies = new HttpCookies(new HashMap<>());
    private String viewResource;
    private String contentType;
    private String messageBody;

    public HttpResponse status(HttpStatus status) {
        this.status = status;
        return this;
    }

    public HttpResponse ok() {
        return status(HttpStatus.OK);
    }

    public HttpResponse redirect(String location) {
        this.location = location;
        return status(HttpStatus.FOUND);
    }

    public HttpResponse setViewResource(String viewResource) {
        this.viewResource = viewResource;
        return this;
    }

    public HttpResponse addSetCookieHeader(String name, HttpCookie value) {
        cookies.setCookie(name, value);
        return this;
    }

    public String getViewResource() {
        return viewResource;
    }

    public HttpResponse setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponse setMessageBody(String messageBody) {
        this.messageBody = messageBody;
        return this;
    }

    public boolean hasViewResource() {
        return viewResource != null;
    }

    public String toMessage() {
        StringJoiner joiner = new StringJoiner("\r\n");
        joiner.add(status.toStatusLine());
        for (String header : toHeaders()) {
            joiner.add(header);
        }
        joiner.add("");
        if (messageBody != null && messageBody.length() > 0) {
            joiner.add(messageBody);
        }
        return joiner.toString();
    }

    private List<String> toHeaders() {
        List<String> headers = new ArrayList<>();
        if (location != null) {
            headers.add(String.format("Location: %s ", location));
        }
        if (cookies.containsCookies()) {
            headers.addAll(cookies.toSetHeaderFormats());
        }
        if (contentType != null) {
            headers.add(String.format("Content-Type: %s ", contentType));
        }
        if (messageBody != null) {
            headers.add(String.format("Content-Length: %d ", messageBody.getBytes().length));
        }
        return headers;
    }
}
