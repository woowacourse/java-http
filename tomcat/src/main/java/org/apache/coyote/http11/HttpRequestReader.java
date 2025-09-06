package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestReader {

    private static final String QUESTION = "?";
    private static final String AND = "&";
    private static final String EQUAL = "=";
    private static final String COLON = ":";

    public HttpRequest read(final InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = readRequestLine(br);
        Map<String, String> headers = readHeaders(br);
        String uri = requestLine.uri();

        int questionIndex = uri.indexOf(QUESTION);
        String path = findPath(questionIndex, uri);

        Map<String, String> queries = findQueries(questionIndex, uri);

        byte[] body = new byte[0];
        String len = headers.get("content-length");
        if (len != null) {
            int contentLength = Integer.parseInt(len);
            body = inputStream.readNBytes(contentLength);
        }

        return new HttpRequest(requestLine, headers, path, queries, body);
    }

    private RequestLine readRequestLine(final BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new BadRequestException("Request line is empty");
        }

        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String uri = parts[1];
        String version = parts[2];
        return new RequestLine(method, uri, version);
    }

    private Map<String, String> readHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int colon = line.indexOf(COLON);
            if (colon < 0) {
                throw new BadRequestException("Header is malformed");
            }
            String name = line.substring(0, colon).toLowerCase();
            String value = line.substring(colon + 1);
            headers.put(name, value);
        }
        return headers;
    }

    private String findPath(final int queryStartIndex, final String uri) {
        if (queryStartIndex < 0) {
            return uri;
        }
        return uri.substring(0, queryStartIndex);
    }

    private Map<String, String> findQueries(final int questionIndex, final String uri) {
        if (questionIndex > 0) {
            String rawQueries = uri.substring(questionIndex + 1);
            return parseQueryString(questionIndex, rawQueries);
        }
        return Map.of();

    }

    private Map<String, String> parseQueryString(final int queryStartIndex, final String uri) {
        if (!uri.contains(QUESTION)) {
            return Map.of();
        }
        String query = uri.substring(queryStartIndex + 1);
        String[] pairs = query.split(AND);
        Map<String, String> queries = new HashMap<>();
        for (String pair : pairs) {
            int equalIndex = pair.indexOf(EQUAL);
            String name = pair.substring(0, equalIndex);
            String value = pair.substring(equalIndex + 1);
            queries.put(name, value);
        }
        return queries;
    }
}
