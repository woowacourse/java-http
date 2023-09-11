package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpCookie;
import org.apache.coyote.http11.common.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.common.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.common.HttpHeaderType.COOKIE;

public class HttpRequestParser {

    private final BufferedReader reader;

    public HttpRequestParser(final BufferedReader reader) {
        this.reader = reader;
    }

    public HttpRequest parse() {
        try {
            final HttpRequestLine requestLine = parseRequestLine();
            final HttpHeaders requestHeaders = parseRequestHeader();
            if (requestHeaders.getHeaderValue(CONTENT_LENGTH) != null) {
                final Map<String, String> body = parseRequestBody(requestHeaders.getHeaderValue(CONTENT_LENGTH));
                return HttpRequest.from(requestLine, requestHeaders, body);
            }
            return HttpRequest.from(requestLine, requestHeaders, new HashMap<>());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequestLine parseRequestLine() throws IOException {
        final String line = reader.readLine();
        final String[] s = line.split(" ");
        if (s[1].contains("?")) {
            String[] split = s[1].split("\\?");
            Map<String, String> queryString = parseQueryString(split[1]);
            return HttpRequestLine.from(s[0], split[0], queryString, s[2]);
        }
        return HttpRequestLine.from(s[0], s[1], s[2]);
    }

    private Map<String, String> parseQueryString(final String queryStrings) {
        final Map<String, String> queries = new HashMap<>();
        return parseUrlEncoded(queryStrings, queries);
    }

    private HttpHeaders parseRequestHeader() throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals(line = reader.readLine()) && line != null) {
            String[] value = line.split(": ");
            if (value.length >= 2) {
                headers.put(value[0], value[1]);
            }
        }
        final HttpHeaders httpHeaders = HttpHeaders.of(headers);
        final HttpCookie httpCookie = HttpCookie.of(headers.get(COOKIE.getName()));
        httpHeaders.setCookie(httpCookie);
        return httpHeaders;
    }

    private Map<String, String> parseRequestBody(final String contentLengthHeader) throws IOException {
        final Map<String, String> body = new HashMap<>();
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return parseUrlEncoded(new String(buffer), body);
    }

    private Map<String, String> parseUrlEncoded(final String queryStrings, final Map<String, String> queries) {
        for (String keyValuePair : queryStrings.split("&")) {
            String[] keyValue = keyValuePair.split("=");
            if (keyValue.length >= 2) {
                queries.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return queries;
    }
}
