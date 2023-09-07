package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponseHeader {

    public static final String TEXT_HTML_CHARSET_UTF_8 = "text/html;charset=utf-8";
    public static final String TEXT_CSS_CHARSET_UTF_8 = "text/css;charset=utf-8";
    private Map<String, String> headers;
 
    public HttpResponseHeader(final String contentType, final String contentLength,
                              final String location, final String setCookie) {
        this.headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        headers.put("Content-Length", contentLength);
        headers.put("Location", location);
        headers.put("Set-Cookie", setCookie);
    }

    @Override
    public String toString() {
        List<String> header = headers.entrySet().stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry -> entry.getKey() + ": " + entry.getValue()
                ).collect(Collectors.toList());
        return String.join(" \r\n", header);
    }
}
