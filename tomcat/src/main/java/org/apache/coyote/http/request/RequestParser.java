package org.apache.coyote.http.request;

import jakarta.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public static Cookie parseCookie(final Map<String, String> headers) {
        if (!headers.containsKey("Cookie")) {
            return new Cookie("emptyCookie", "");
        }
        final String cookie = headers.get("Cookie");
        final String[] cookieParts = cookie.split("=");
        return new Cookie(cookieParts[0].trim(), cookieParts[1].trim());
    }

    public static String parseBody(final Map<String, String> headers, final BufferedReader bufferedReader)
            throws IOException {
        if (!headers.containsKey("Content-Length")) {
            return "";
        }

        final int contentLength = Integer.parseInt(headers.get(("Content-Length")));
        return getRequestBody(contentLength, bufferedReader);
    }


    public static String getRequestBody(int contentLength, BufferedReader br) throws IOException {
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return new String(buffer);
    }

}
