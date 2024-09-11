package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.common.HttpCookie;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers;

    public RequestHeaders(List<String> headers) {
        this.headers = toMap(headers);
    }

    private Map<String, String> toMap(List<String> headerLines) {
        Map<String, String> headers = new LinkedHashMap<>();

        for (String headerLine : headerLines) {
            String[] headerInfo = headerLine.split(HEADER_DELIMITER);
            headers.put(headerInfo[0].trim(), headerInfo[1].trim());
        }

        return headers;
    }

    public boolean hasJSessionCookie() {
        if (headers.containsKey("Cookie")) {
            return headers.get("Cookie").contains("JSESSIONID");
        }
        return false;
    }

    public String getJSessionId() {
        HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
        return httpCookie.get("JSESSIONID");
    }

    public Session getSession() {
        HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
        String jsessionid = httpCookie.get("JSESSIONID");
        return new Session(jsessionid);
    }

    public int getContentLength() {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }
}
