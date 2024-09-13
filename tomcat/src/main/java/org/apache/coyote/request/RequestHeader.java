package org.apache.coyote.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.coockie.HttpCookie;
import org.apache.coyote.http.HeaderName;

public class RequestHeader {

    private final Map<String, String> header;
    private final HttpCookie httpCookie;

    public RequestHeader(List<String> headerLines) {
        this.header = mapHeader(headerLines);
        this.httpCookie = mapCookie();
    }

    private HttpCookie mapCookie() {
        if (header.containsKey(HeaderName.COOKIE.getValue())) {
            return new HttpCookie(header.get(HeaderName.COOKIE.getValue()));
        }
        return new HttpCookie();
    }

    private Map<String, String> mapHeader(List<String> headerLines) {
        Map<String, String> rawHeader = new HashMap<>();

        for (String line : headerLines) {
            String[] headerEntry = line.split(": ", 2);
            rawHeader.put(headerEntry[0], headerEntry[1]);
        }
        return rawHeader;
    }

    public boolean hasSession() {
        return header.containsKey(HeaderName.COOKIE.getValue()) && httpCookie.hasSessionId();
    }

    public String getSessionId() {
        return httpCookie.getSessionId();
    }

    public String getHttpCookie() {
        return httpCookie.getResponse();
    }
}
