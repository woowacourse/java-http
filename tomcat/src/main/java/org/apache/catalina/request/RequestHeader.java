package org.apache.catalina.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.catalina.HeaderName;
import org.apache.catalina.coockie.HttpCookie;

public class RequestHeader {

    public static final String NAME_VALUE_DELIMITER = ": ";
    public static final int NAME_VALUE_COUNT = 2;
    public static final int NAME_INDEX = 0;
    public static final int VALUE_INDEX = 1;


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
            String[] headerEntry = line.split(NAME_VALUE_DELIMITER, NAME_VALUE_COUNT);
            rawHeader.put(headerEntry[NAME_INDEX], headerEntry[VALUE_INDEX]);
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

    public boolean hasCookie() {
        return httpCookie.hasCookie();
    }
}
