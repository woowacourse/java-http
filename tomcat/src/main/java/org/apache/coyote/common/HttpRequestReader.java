package org.apache.coyote.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestReader {

    private static final String HEADER_DELIMITER = ": ";
    private static final String QUERY_PARAM_SEPARATOR = "&";
    private static final String QUERY_PARAM_DELIMITER = "=";
    private static final String QUERY_PARAM_PREFIX = "?";

    public static HttpRequest read(final BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        String method = extractMethod(requestLine);
        String path = extractPath(requestLine);
        String protocol = extractProtocol(requestLine);
        Map<String, String> headers = extractHeaders(bufferedReader);
        Map<String, String> queryParams = extractQueryParams(path);

        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return new HttpRequest(method, path, protocol, headers, queryParams, null);
        }

        String body = extractBody(bufferedReader, Integer.parseInt(contentLength));
        return new HttpRequest(method, path, protocol, headers, queryParams, body);
    }

    private static String extractMethod(final String line) {
        return line.split(" ")[0];
    }

    private static String extractPath(final String line) {
        return line.split(" ")[1];
    }

    private static String extractProtocol(final String line) {
        return line.split(" ")[2];
    }

    private static Map<String, String> extractHeaders(final BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            if (line.contains(HEADER_DELIMITER)) {
                String[] headerParts = line.split(HEADER_DELIMITER);
                headers.put(headerParts[0], headerParts[1]);
            }
            line = bufferedReader.readLine();
        }
        return headers;
    }

    private static Map<String, String> extractQueryParams(final String uri) {
        if (!uri.contains("?")) return null;

        Map<String, String> queryParams = new HashMap<>();

        int index = uri.indexOf(QUERY_PARAM_PREFIX);
        String queryString = uri.substring(index + 1);
        String[] queries = queryString.split(QUERY_PARAM_SEPARATOR);

        for (String query : queries) {
            String[] keyValues = query.split(QUERY_PARAM_DELIMITER);
            String key = keyValues[0];
            String value = keyValues[1];
            queryParams.put(key, value);
        }

        return queryParams;
    }

    private static String extractBody(final BufferedReader bufferedReader, final int length) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        char[] chars = new char[length];
        int charsRead = bufferedReader.read(chars, 0, length);
        stringBuilder.append(chars, 0, charsRead);

        return stringBuilder.toString();
    }
}
