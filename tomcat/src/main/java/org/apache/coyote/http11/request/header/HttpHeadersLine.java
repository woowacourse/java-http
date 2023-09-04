package org.apache.coyote.http11.request.header;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeadersLine {
    private final Map<String , String> headers;

    private HttpHeadersLine(final Map<String , String> headers) {
        this.headers = headers;
    }

    public static HttpHeadersLine from(final String[] httpHeadersLine) {
        return new HttpHeadersLine(Arrays.stream(httpHeadersLine)
                .map(header -> header.split(":"))
                .collect(Collectors.toMap(key -> key[0], value -> value[1])));
    }

    public static HttpHeadersLine from(final Map<String, String> httpHeaders) {
        return new HttpHeadersLine(httpHeaders);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length").trim());
    }

    public String getCookie() {
        return headers.get("Cookie").trim();
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
