package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String LOCATION = "Location";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Set-Cookie";

    private StatusLine statusLine;
    private Map<String, String> headers = new LinkedHashMap<>();
    private String responseBody;

    public void setStatus200() {
        statusLine = new StatusLine("HTTP/1.1", "200", "OK");
    }

    public void setStatus302() {
        statusLine = new StatusLine("HTTP/1.1", "302", "Found");
    }

    public void setStatus404() {
        statusLine = new StatusLine("HTTP/1.1", "404", "Not Found");
    }

    public void setStatus405() {
        statusLine = new StatusLine("HTTP/1.1", "405", "Method Not Allowed");
    }

    public void setLocation(String locationPath) {
        headers.put(LOCATION, locationPath);
    }

    public void setContentType(String contentType) {
        headers.put(CONTENT_TYPE, contentType);
    }

    public void setResponseBody(String content) {
        responseBody = content;
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
    }

    public void setCookie(HttpCookie cookie) {
        headers.put(COOKIE, "JSESSIONID=" + cookie.getJsessionId());
    }

    public String getResponse() {
        StringBuilder header = new StringBuilder();
        if (headers.containsKey(LOCATION)) {
            header.append(LOCATION + ": ").append(headers.get(LOCATION) + " ").append(LINE_SEPARATOR);
        }
        if (headers.containsKey(CONTENT_TYPE)) {
            header.append(CONTENT_TYPE + ": ").append(headers.get(CONTENT_TYPE) + " ").append(LINE_SEPARATOR);
        }
        if (headers.containsKey(CONTENT_LENGTH)) {
            header.append(CONTENT_LENGTH + ": ").append(headers.get(CONTENT_LENGTH) + " ").append(LINE_SEPARATOR);
        }
        if (headers.containsKey(COOKIE)) {
            header.append(COOKIE + ": ").append(headers.get(COOKIE) + " ").append(LINE_SEPARATOR);
        }

        String response = String.join(LINE_SEPARATOR,
                statusLine.getStatusLine(),
                header.toString(),
                responseBody
        );

        log.info("[Response] Status Line: {}", statusLine.getStatusLine());
        log.info("[Response] Headers: {}", header);

        return response;
    }
}
