package org.apache.coyote.servlet.response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import org.apache.coyote.servlet.cookie.HttpCookie;
import org.apache.coyote.servlet.cookie.HttpCookies;
import org.apache.coyote.support.HttpStatus;

public class HttpResponse {

    private HttpStatus status;
    private String location;
    private final HttpCookies cookies = new HttpCookies(new HashMap<>());
    private String contentType;
    private String messageBody;

    public void update(ResponseEntity responseEntity) {
        this.status = responseEntity.getStatus();
        this.location = responseEntity.getLocation();
        this.contentType = responseEntity.getContentType();
        this.messageBody = responseEntity.getMessageBody();
    }

    public void addSetCookieHeader(String name, HttpCookie value) {
        cookies.setCookie(name, value);
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
