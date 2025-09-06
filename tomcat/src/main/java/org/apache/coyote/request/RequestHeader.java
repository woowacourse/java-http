package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.catalina.servlet.cookie.HttpCookie;
import org.apache.catalina.servletContainer.session.Session;
import org.apache.coyote.response.responseHeader.HttpHeaders;

public class RequestHeader {

    public static final String HEADER_SEPARATOR = ":";
    private final Map<HttpHeaders, String> headerValue;

    public RequestHeader(final List<String> headerLines) {
        this.headerValue = headerLines.stream()
                .map(headerLine -> headerLine.split(HEADER_SEPARATOR))
                .collect(Collectors.toMap(
                        keyValue -> HttpHeaders.find(keyValue[0]),
                        keyValue -> keyValue[1]
                ));
    }

    public int getBodyLength() {
        if (headerValue.containsKey(HttpHeaders.CONTENT_LENGTH)) {
            return Integer.parseInt(headerValue.get(HttpHeaders.CONTENT_LENGTH).trim());
        }
        return 0;
    }

    public Session getSession() {
        String cookie = headerValue.get(HttpHeaders.COOKIE);
        HttpCookie httpCookie = new HttpCookie(cookie);

        return httpCookie.getSession();
    }
}
