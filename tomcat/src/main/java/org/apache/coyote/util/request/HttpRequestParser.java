package org.apache.coyote.util.request;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.util.Cookie;

public class HttpRequestParser {

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        String requestLineString = readLine(inputStream);
        if (requestLineString == null || requestLineString.isEmpty()) {
            return null;
        }

        RequestLine requestLine = parseRequestLine(requestLineString);
        Map<String, String> headers = parseHeaders(inputStream);
        Map<String, String> body = parseBody(inputStream, headers);
        Cookie cookie = Cookie.parse(headers.get("cookie"));

        return new HttpRequest(requestLine, headers, body, cookie);
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int current;
        while ((current = inputStream.read()) != -1) {
            if (current == '\r') {
                int next = inputStream.read();
                if (next == '\n') {
                    break;
                }
                bos.write(current);
                if (next != -1) {
                    bos.write(next);
                }
                continue;
            }
            bos.write(current);
        }
        return bos.toString(StandardCharsets.UTF_8);
    }

    private static RequestLine parseRequestLine(String requestLineString) throws IOException {
        String[] tokens = requestLineString.split(" ");
        if (tokens.length != 3) {
            throw new IOException("Invalid Request-Line: " + requestLineString);
        }
        String method = tokens[0];
        String[] uriTokens = tokens[1].split("\\?", 2);
        String path = uriTokens[0];
        Map<String, String> queryParams;
        if (uriTokens.length == 2) {
            queryParams = parseQueryString(uriTokens[1]);
        } else {
            queryParams = new HashMap<>();
        }
        String version = tokens[2];
        return new RequestLine(method, path, version, queryParams);
    }

    private static Map<String, String> parseHeaders(final InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            String[] headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                String key = headerParts[0].trim().toLowerCase();
                String value = headerParts[1].trim();
                headers.put(key, value);
            }
        }
        return headers;
    }

    private static Map<String, String> parseBody(InputStream inputStream, Map<String, String> headers)
            throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        if (contentLength == 0) {
            return new HashMap<>();
        }
        byte[] bodyBytes = new byte[contentLength];
        int bytesRead = 0;
        while (bytesRead < contentLength) {
            int result = inputStream.read(bodyBytes, bytesRead, contentLength - bytesRead);
            if (result == -1) {
                break;
            }
            bytesRead += result;
        }
        String bodyString = new String(bodyBytes, StandardCharsets.UTF_8);
        return parseQueryString(bodyString);
    }

    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> queryParams = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return queryParams;
        }
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length > 0) {
                String key = urlDecode(keyValue[0]);
                String value;
                if (keyValue.length > 1) {
                    value = urlDecode(keyValue[1]);
                } else {
                    value = "";
                }
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    private static String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
