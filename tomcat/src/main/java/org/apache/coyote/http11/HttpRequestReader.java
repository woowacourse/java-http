package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestReader {

    public static HttpRequest read(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            return null;
        }
        String[] parts = requestLine.split(" ");
        String method = parts[0];

        String uri = parts[1];
        String path = extractPathByUri(uri);
        String httpVersion = parts[2];
        Map<String, String> queryString = parseQueryStringByUri(uri);
        Map<String, String> httpRequestHeaders = readHttpRequestHeaders(bufferedReader);
        Map<String, String> requestBody = readRequestBody(bufferedReader, httpRequestHeaders);
        HttpCookie httpCookie = parseHttpCookie(httpRequestHeaders);

        return new HttpRequest(
                method,
                path,
                queryString,
                httpVersion,
                httpRequestHeaders,
                requestBody,
                httpCookie
        );
    }

    private static Map<String, String> readRequestBody(
            final BufferedReader bufferedReader,
            final Map<String, String> httpRequestHeaders
    ) throws IOException {
        String contentLengthHeader = httpRequestHeaders.get("Content-Length");
        if (contentLengthHeader == null) {
            return new HashMap<>();
        }
        int contentLength = Integer.parseInt(contentLengthHeader.trim());

        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBodyEncoded = new String(buffer);
        return parseUrlEncoded(requestBodyEncoded);
    }

    private static Map<String, String> readHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequestHeaders = new HashMap<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            int idx = line.indexOf(":");
            if (idx > 0) {
                String name = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                httpRequestHeaders.put(name, value);
            }
        }
        return httpRequestHeaders;
    }

    private static HttpCookie parseHttpCookie(Map<String, String> httpRequestHeaders) {
        String cookieHeader = httpRequestHeaders.get("Cookie");
        HttpCookie httpCookie = new HttpCookie();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return httpCookie;
        }
        Arrays.stream(cookieHeader.split(";"))
                .map(String::trim)
                .map(kv -> kv.split("=", 2))
                .filter(kv -> kv.length == 2)
                .forEach(kv -> httpCookie.add(kv[0], kv[1]));
        return httpCookie;
    }

    private static String extractPathByUri(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private static Map<String, String> parseQueryStringByUri(final String uri) {
        int index = uri.indexOf("?");
        if (index == -1) {
            return Map.of();
        }
        String queryString = uri.substring(index + 1);
        return parseQueryString(queryString);
    }

    private static Map<String, String> parseQueryString(final String querystring) {
        return parseUrlEncoded(querystring);
    }

    private static Map<String, String> parseUrlEncoded(final String querystring) {
        return Arrays.stream(querystring.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> urlDecode(parts[0]),
                        parts -> urlDecode(parts[1]))
                );
    }

    private static String urlDecode(String value) {
        try {
            return java.net.URLDecoder.decode(value, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }
}
