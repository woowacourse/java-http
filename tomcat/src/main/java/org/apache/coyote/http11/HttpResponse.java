package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.LOCATION;
import static org.apache.coyote.http11.HttpHeader.SET_COOKIE;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private final Map<String, String> attribute = new LinkedHashMap<>();
    private HttpStatus status;
    private String responseBody;

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void sendRedirect(String redirectionUrl) {
        attribute.put(LOCATION.getValue(), redirectionUrl);
    }

    public void addCookie(String cookie) {
        attribute.put(SET_COOKIE.getValue(), cookie);
    }

    public void setHeader(String key, String value) {
        attribute.put(key, value);
    }

    public String toString() {
        return String.join("\r\n",
                PROTOCOL + " " + status.getValue() + " " + status.name() + " ",
                attribute.entrySet()
                        .stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                        .collect(Collectors.joining("\r\n")),
                "",
                responseBody
        );
    }

}
