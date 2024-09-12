package com.techcourse.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class HttpRequestParser {

    private static final String START_LINE_SEPARATOR = " ";
    private static final String QUERY_PARAMETER_SEPARATOR = "?";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String PARAMETER_KEY_VALUE_SEPARATOR = "=";
    private static final String HEADER_KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

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
        String[] startLineParts = startLine.split(START_LINE_SEPARATOR);
        String method = startLineParts[0];
        String uri = URLDecoder.decode(startLineParts[1], StandardCharsets.UTF_8);

        request.setMethod(method);
        request.setUri(uri);

        int queryIndex = uri.indexOf(QUERY_PARAMETER_SEPARATOR);
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

        String[] parameters = parameterString.split(PARAMETER_SEPARATOR);

        for (String parameter : parameters) {
            String[] keyValue = parameter.split(PARAMETER_KEY_VALUE_SEPARATOR);
            if (keyValue.length == 2) {
                request.setParameter(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
            }
        }
    }

    private static void parseHeaders(BufferedReader reader, HttpRequest request) throws IOException {
        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            String[] keyValue = line.split(HEADER_KEY_VALUE_SEPARATOR);
            if (keyValue.length == 2) {
                request.setHeader(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
            }
        }

        if (request.getHeader(HttpHeaders.COOKIE) != null) {
            parseCookie(request);
        }
    }

    private static void parseCookie(HttpRequest request) {
        String cookieString = request.getHeader(HttpHeaders.COOKIE);

        String[] cookieArray = cookieString.split(HttpCookie.SEPARATOR);
        for (String cookiePair : cookieArray) {
            String[] keyValue = cookiePair.split(HttpCookie.KEY_VALUE_SEPARATOR);
            if (keyValue.length == 2) {
                request.setCookie(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
            }
        }
    }

    private static void parseBody(BufferedReader reader, HttpRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength == 0) {
            return;
        }

        char[] bodyChars = new char[contentLength];
        if (reader.read(bodyChars, 0, contentLength) != contentLength) {
            throw new IOException("Failed to read the entire request body");
        }

        String body = new String(bodyChars);
        if (request.hasContentType(HttpHeaders.APPLICATION_X_WWW_FORM_URLENCODED)) {
            parseParameters(request, URLDecoder.decode(body, StandardCharsets.UTF_8));
            return;
        }
        request.setBody(body);
    }
}
