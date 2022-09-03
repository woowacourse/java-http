package org.apache.coyote.http11.request;

import static org.apache.catalina.utils.Parser.removeBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaderType;

public class HttpHeaders {
    private final Map<HttpHeaderType, HttpHeader> headers;

    private HttpHeaders(final Map<HttpHeaderType, HttpHeader> headers) {
        this.headers = headers;
    }

    public static HttpHeaders of(final BufferedReader bufferedReader) throws IOException {
        return new HttpHeaders(readAllHeaders(bufferedReader));
    }

    public static HttpHeaders of(final HttpHeader... httpHeaders) {
        final Map<HttpHeaderType, HttpHeader> headers = new LinkedHashMap<>();
        for (HttpHeader httpHeader : httpHeaders) {
            headers.put(httpHeader.getHttpHeaderType(), httpHeader);
        }
        return new HttpHeaders(headers);
    }

    private static Map<HttpHeaderType, HttpHeader> readAllHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<HttpHeaderType, HttpHeader> headers = new LinkedHashMap<>();

        while (true) {
            final String line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }
            final List<String> header = parseHeader(line);
            final String headerType = removeBlank(header.get(0));
            final String headerValue = removeBlank(header.get(1));
            final HttpHeaderType httpHeaderType = HttpHeaderType.of(headerType);
            headers.put(httpHeaderType, HttpHeader.of(httpHeaderType, headerValue));
        }

        return headers;
    }

    private static List<String> parseHeader(final String line) {
        final List<String> header = List.of(line.split(":"));
        validateHeader(header);
        return header;
    }

    private static void validateHeader(final List<String> header) {
        if (header.size() < 2) {
            throw new IllegalArgumentException("요청 정보가 잘못되었습니다.");
        }
    }

    public Map<HttpHeaderType, HttpHeader> getHeaders() {
        return headers;
    }

    public boolean contains(final HttpHeaderType contentLength) {
        return headers.containsKey(contentLength);
    }

    public HttpHeader get(final HttpHeaderType contentLength) {
        return headers.get(contentLength);
    }

    public Set<HttpHeaderType> keySet() {
        return headers.keySet();
    }
}
