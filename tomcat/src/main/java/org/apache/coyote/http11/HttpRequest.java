package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final String QUERY_SEPARATOR = "?";
    private static final String HEADER_SEPARATOR = ":";
    private static final String CONTENT_LENGTH = "content-length";

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(String method, String path, Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        String[] tokens = requestLine.split(REQUEST_LINE_SEPARATOR);
        String method = tokens[0];
        String path = parsePath(tokens[1]);

        Map<String, String> headers = parseHeaders(reader);
        String body = parseBody(reader, headers);

        return new HttpRequest(method, path, headers, body);
    }

    private static String parsePath(String uri) {
        int index = uri.indexOf(QUERY_SEPARATOR);
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int index = line.indexOf(HEADER_SEPARATOR);
            String name = line.substring(0, index).trim().toLowerCase();
            String value = line.substring(index + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private static String parseBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return "";
        }
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        reader.read(buffer, 0, length);
        return new String(buffer);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getBody() {
        return body;
    }
}
