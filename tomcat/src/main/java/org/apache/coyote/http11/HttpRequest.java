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
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;

    private HttpRequest(String method, String path, Map<String, String> headers, String body, Map<String, String> parameters) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.parameters = parameters;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        String[] tokens = requestLine.split(REQUEST_LINE_SEPARATOR);
        String method = tokens[0];
        String path = parsePath(tokens[1]);

        Map<String, String> headers = parseHeaders(reader);
        String body = parseBody(reader, headers);

        Map<String, String> parameters = parseParameters(body);

        return new HttpRequest(method, path, headers, body, parameters);
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

    private static Map<String, String> parseParameters(String body) {
        Map<String, String> parameters = new HashMap<>();
        if (body.isEmpty()) {
            return parameters;
        }
        for (String pair : body.split(PARAMETER_SEPARATOR)) {
            String[] keyValue = pair.split(KEY_VALUE_SEPARATOR);
            parameters.put(keyValue[0], keyValue[1]);
        }
        return parameters;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }
}
