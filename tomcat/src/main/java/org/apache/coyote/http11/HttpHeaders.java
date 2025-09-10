package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;

public class HttpHeaders {

    private List<HttpHeader> headers = new ArrayList<>();

    public HttpHeaders(List<String> headerStrings) {
        for (String headerString : headerStrings) {
            String[] splitHeader = headerString.split(":");
            if (splitHeader.length == 2) {
                HttpHeader header = new HttpHeader(splitHeader[0], splitHeader[1].trim());
                headers.add(header);
            }
        }
    }

    public HttpCookie getCookie() {
        String cookieHeaderName = "cookie";
        List<HttpHeader> cookieHeaders = findHeaderByName(cookieHeaderName);
        return new HttpCookie(cookieHeaders);
    }

    public int getContentLength() {
        List<HttpHeader> headers = findHeaderByName("content-length");
        if (headers.size() > 1) {
            throw new IllegalArgumentException("More than one content length header");
        }
        if (headers.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(headers.getFirst().getValue());
    }

    public List<HttpHeader> findHeaderByName(String name) {
        return headers.stream()
            .filter(header -> header.nameEquals(name))
            .toList();
    }
}
