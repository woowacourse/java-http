package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.HttpHeaderType.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeaderType.COOKIE;

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
        String[] s = line.split(" ");
        return HttpRequestLine.from(s[0], s[1], s[2]);
    }

    private HttpHeaders parseRequestHeader() throws IOException {
        // TODO: Change to MultiValueMap
        final Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals(line = reader.readLine())) {
            String[] value = line.split(": ");
            headers.put(value[0], value[1]);
        }
        final HttpHeaders httpHeaders = HttpHeaders.of(headers);
        final HttpCookie httpCookie = HttpCookie.of(headers.get(COOKIE.getName()));
        httpHeaders.setCookie(httpCookie);
        return httpHeaders;
    }

    private Map<String, String> parseRequestBody(final String contentLengthHeader) throws IOException {
        // TODO: Change to MultiValueMap
        final Map<String, String> body = new HashMap<>();
        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);

        // TODO: Query Parse
        for (String temp : new String(buffer).split("&")) {
            String[] value = temp.split("=");
            body.put(value[0], URLDecoder.decode(value[1], "UTF-8"));
        }
        return body;
    }
}
