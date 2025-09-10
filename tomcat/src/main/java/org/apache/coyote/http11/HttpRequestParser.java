package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.session.HttpCookie;

public final class HttpRequestParser {

    public HttpRequest parse(Http11InputBuffer http11InputBuffer) throws IOException {
        String requestLine = http11InputBuffer.readLine();

        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("빈 요청입니다.");
        }

        final String[] parts = requestLine.split(" ", 3);
        if (parts.length < 3) {
            throw new IOException("잘못된 요쳥입니다.");
        }
        final String method = parts[0];
        final String target = parts[1];
        final String version = parts[2];
        Map<String, String> headers = new LinkedHashMap<>();

        while (true) {
            String line = http11InputBuffer.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            int idx = line.indexOf(':');
            if (idx <= 0) {
                continue;
            }
            String name = line.substring(0, idx).trim();
            String value = line.substring(idx + 1).trim();
            headers.put(name.toLowerCase(), value);
        }
        String cookieHeader = headers.get("cookie");
        HttpCookie httpCookie = new HttpCookie(cookieHeader);
        String uri, queryString = "";
        int q = target.indexOf('?');
        if (q >= 0) {
            uri = target.substring(0, q);
            queryString = target.substring(q + 1);
        } else {
            uri = target;
        }
        Map<String, String> query = parseQuery(queryString);

        byte[] body = new byte[0];
        int contentLength = headers.containsKey("content-length") ? Integer.parseInt(headers.get("content-length")) : 0;
        if (contentLength > 0) {
            body = http11InputBuffer.readBytes(contentLength);
        }
        return new HttpRequest(method, uri, version, headers, query, body, parseQuery(new String(body, UTF_8)),
                httpCookie);
    }

    private Map<String, String> parseQuery(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return Map.of();
        }
        Map<String, String> map = new LinkedHashMap<>();
        for (String pair : queryString.split("&")) {
            if (pair.isEmpty()) {
                continue;
            }
            int equalIndex = pair.indexOf('=');
            String k = equalIndex >= 0 ? pair.substring(0, equalIndex) : pair;
            String v = equalIndex >= 0 ? pair.substring(equalIndex + 1) : "";
            map.put(urlDecode(k), urlDecode(v));
        }
        return map;
    }

    private String urlDecode(String string) {
        return URLDecoder.decode(string, UTF_8);
    }
}

