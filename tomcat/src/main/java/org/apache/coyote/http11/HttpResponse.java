package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeader.LOCATION;
import static org.apache.coyote.http11.HttpHeader.SET_COOKIE;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String PROTOCOL = "HTTP/1.1";
    private final Map<String, String> attribute = new LinkedHashMap<>();
    private final Cookie cookie;
    private HttpStatus status;
    private String responseBody;

    public HttpResponse() {
        this.cookie = new Cookie(new HashMap<>());
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void sendRedirect(String redirectionUrl) {
        attribute.put(LOCATION.getValue(), redirectionUrl);
    }

    public void addCookie(String key, String value) {
        cookie.put(key, value);
    }

    public void setHeader(String key, String value) {
        attribute.put(key, value);
    }

    private String getAttributeString() {
        return String.join("\r\n",
                attribute.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n")),
                getCookieString());
    }

    private String getCookieString() {
        Map<String, String> values = cookie.getValues();

        if (values.isEmpty()) {
            return "";
        }

        StringJoiner cookieString = new StringJoiner(";");
        values.forEach((key, value) -> cookieString.add(String.format("%s=%s", key, value)));

        return SET_COOKIE.getValue() + ": " + cookieString;
    }

    public String toString() {
        return String.join("\r\n",
                PROTOCOL + " " + status.getValue() + " " + status.name() + " ",
                getAttributeString(),
                responseBody
        );
    }

}
