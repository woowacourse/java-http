package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.List;

public class Http11RequestHeaders {
    private final List<Header> headers;

    private Http11RequestHeaders(final List<Header> headers) {
        this.headers = headers;
    }

    public static Http11RequestHeaders extractHeaders(final List<String> lines) {
        List<Header> headerList = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] splittedLines = line.split(":", 2);
            if (splittedLines.length == 2) {
                headerList.add(new Header(splittedLines[0].trim(), splittedLines[1].trim()));
            }
        }

        return new Http11RequestHeaders(headerList);
    }

    public int getContentLength() {
        return headers.stream()
                .filter(Header::isContentLength)
                .map(Header::getValue)
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    public String getSessionId() {
        return headers.stream()
                .filter(Header::isCookieHeader)
                .findFirst()
                .map(Header::getValue)
                .map(Http11Cookie::new)
                .map(cookie -> cookie.get("JSESSIONID"))
                .orElse(null);
    }
}
