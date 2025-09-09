package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        final String requestLine = readRequestLine(bufferedReader);
        final String[] requestParts = requestLine.split(" ");
        final HttpRequestMethod method = HttpRequestMethod.valueOf(requestParts[0]);
        final String uri = requestParts[1];
        final String path = parsePath(uri);

        final Map<String, String> headers = readHeaders(bufferedReader);
        final HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));

        final String body = readBody(headers, bufferedReader);
        final Map<String, String> formParams = readFormParams(headers, body);

        return new HttpRequest(method, path, httpCookie, formParams);
    }

    private String readRequestLine(BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid: request line is null or empty.");
        }
        return requestLine;
    }

    private Map<String, String> readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ");
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
            headers.put(headerParts[0], headerParts[1]);
        }
        return headers;
    }

    private String readBody(Map<String, String> headers, BufferedReader bufferedReader) throws IOException {
        String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);

        return new String(body);
    }

    private Map<String, String> readFormParams(Map<String, String> headers, String body) {
        if (body == null || body.isEmpty()) {
            return Collections.emptyMap();
        }

        String contentType = headers.get("Content-Type");
        if (contentType.equals("application/x-www-form-urlencoded")) {
            Map<String, String> formParams = new HashMap<>();
            for (String pair : body.split("&")) {
                final String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    formParams.put(keyValue[0], keyValue[1]);
                }
            }
            return formParams;
        }
        return Collections.emptyMap();
    }

    private String parsePath(String uri) {
        if (uri.contains("?")) {
            return uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }
}
