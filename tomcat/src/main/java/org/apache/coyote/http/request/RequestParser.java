package org.apache.coyote.http.request;

import jakarta.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RequestParser {

    public static RequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String requestLines = bufferedReader.readLine();
        final String[] startLineParts = requestLines.split(" ");
        final String method = startLineParts[0];
        final String url = startLineParts[1];
        final String protocol = startLineParts[2];
        return new RequestLine(method, url, protocol);
    }

    public static Map<String, String> parseHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] split = line.split(":");
            headers.put(split[0].trim(), split[1].trim());
        }
        return headers;
    }

    public static Optional<Cookie> parseCookie(final Map<String, String> headers) {
        if (!headers.containsKey("Cookie")) {
            return Optional.empty();
        }
        final String cookie = headers.get("Cookie");
        final String[] cookieParts = cookie.split("=");
        return Optional.of(new Cookie(cookieParts[0].trim(), cookieParts[1].trim()));
    }

    public static Map<String, String> parseBody(final Map<String, String> headers, final BufferedReader bufferedReader)
            throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return Map.copyOf(Map.of());
        }

        final int contentLength = Integer.parseInt(headers.get(("Content-Length")));
        final String bodyRaw = getRequestBodyRaw(contentLength, bufferedReader);

        final String[] queryStringParts = bodyRaw.split("&");
        final Map<String, String> body = new HashMap<>();
        for (String param : queryStringParts) {
            String[] kv = param.split("=");
            body.put(kv[0].trim(), kv[1].trim());
        }
        return Map.copyOf(body);
    }

    public static String getRequestBodyRaw(int contentLength, BufferedReader br) throws IOException {
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return new String(buffer);
    }

}
