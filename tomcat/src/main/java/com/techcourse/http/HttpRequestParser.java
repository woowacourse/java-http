package com.techcourse.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest();

        parseStartLine(reader, request);
        parseHeaders(reader, request);
        parseBody(reader, request);

        return request;
    }

    private static void parseStartLine(BufferedReader reader, HttpRequest request) throws IOException {
        String startLine = reader.readLine();
        String[] startLineParts = startLine.split(" ");
        String method = startLineParts[0];
        String uri = URLDecoder.decode(startLineParts[1], StandardCharsets.UTF_8);

        request.setMethod(method);
        request.setUri(uri);

        int queryIndex = uri.indexOf("?");
        if (queryIndex == -1) {
            request.setPath(uri);
            return;
        }
        request.setPath(uri.substring(0, queryIndex));
        String parameterString = uri.substring(queryIndex + 1);
        parseParameters(request, parameterString);
    }

    private static void parseParameters(HttpRequest request, String parameterString) {
        if (parameterString.isBlank()) {
            return;
        }

        String[] parameters = parameterString.split("&");

        for (String parameter : parameters) {
            String[] keyValue = parameter.split("=");
            request.setParameter(keyValue[0], keyValue[1]);
        }
    }

    private static void parseHeaders(BufferedReader reader, HttpRequest request) throws IOException {
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] headerParts = line.split(": ");
            request.setHeader(headerParts[0], headerParts[1]);
        }

        if (request.getHeader("Cookie") != null) {
            HttpCookie cookie = parseCookie(request.getHeader("Cookie"));
            request.setCookie(cookie);
        }
    }

    private static HttpCookie parseCookie(String cookieString) {
        Map<String, String> cookies = new HashMap<>();

        String[] cookieArray = cookieString.split("; ");
        for (String cookiePair : cookieArray) {
            String[] pair = cookiePair.split("=");
            cookies.put(pair[0], pair[1]);
        }
        return new HttpCookie(cookies);
    }

    private static void parseBody(BufferedReader reader, HttpRequest request) throws IOException {
        String contentLengthHeader = request.getHeader("Content-Length");
        if (contentLengthHeader == null) {
            return;
        }

        int contentLength = Integer.parseInt(contentLengthHeader);
        char[] bodyChars = new char[contentLength];
        int readChars = reader.read(bodyChars, 0, contentLength);

        if (readChars != contentLength) {
            throw new IOException("Failed to read the entire request body");
        }

        request.setBody(URLDecoder.decode(new String(bodyChars), StandardCharsets.UTF_8));
    }
}
