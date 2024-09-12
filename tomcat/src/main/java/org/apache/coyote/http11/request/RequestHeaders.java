package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.response.HttpHeader;
import org.apache.coyote.http11.utils.Cookies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ":";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int DEFAULT_CONTENT_LENGTH = 0;

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headers) {
        this.headers = toMap(headers);
    }

    private Map<String, String> toMap(List<String> headerLines) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (String headerLine : headerLines) {
            String[] headerInfo = headerLine.split(HEADER_DELIMITER);
            headers.put(headerInfo[KEY_INDEX].trim(), headerInfo[VALUE_INDEX].trim());
        }

        return headers;
    }

    public boolean hasJSessionCookie() {
        HttpHeader cookieHeader = HttpHeader.COOKIE;

        if (has(cookieHeader)) {
            return get(cookieHeader).contains(Cookies.JSESSIONID);
        }
        return false;
    }

    public boolean has(HttpHeader httpHeader) {
        return headers.containsKey(httpHeader.getName());
    }

    public String get(HttpHeader httpHeader) {
        return headers.get(httpHeader.getName());
    }

    public Session getSession() {
        String jSessionId = getJSessionId();
        return new Session(jSessionId);
    }

    public String getJSessionId() {
        HttpCookie httpCookie = new HttpCookie(get(HttpHeader.COOKIE));
        return httpCookie.get(Cookies.JSESSIONID);
    }

    public int getContentLength() {
        String contentLength = get(HttpHeader.CONTENT_LENGTH);

        if (contentLength == null) {
            return DEFAULT_CONTENT_LENGTH;
        }
        return Integer.parseInt(contentLength);
    }
}
