package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private static final String CONTENT_LENGTH_HEADER = "Content-Length";

    private final String method;                      // GET, POST
    private final String path;                        // /login
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;

    public HttpRequest(String method, String path, Map<String, String> parameters, Map<String, String> headers,
                       String body) {
        this.method = method;
        this.path = path;
        this.parameters = parameters;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }
        final var method = parseMethod(requestLine);
        final var uri = parseUri(requestLine);
        final var pathAndQueryString = splitPathAndQueryString(uri);
        final var path = pathAndQueryString[0];

        var queryString = "";
        if (pathAndQueryString.length == 2) {
            queryString = pathAndQueryString[1];
        }

        final var parameters = parseQueryParameters(queryString);
        final var headers = readHeaders(reader);
        final var body = readBody(reader, headers);
        parameters.putAll(parseQueryParameters(body));

        return new HttpRequest(method, path, parameters, headers, body);
    }

    private static String[] splitPathAndQueryString(final String uri) {
        return uri.split("\\?", 2);
    }

    private static String parseMethod(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        return tokens[0];
    }

    private static String parseUri(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        return tokens[1];
    }

    private static Map<String, String> parseQueryParameters(final String queryString) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        if (queryString.isEmpty()) {
            return parameters;
        }
        final String[] pairs = queryString.split("&");
        for (int i = 0; i < pairs.length; i++) {
            final String[] nameAndValue = pairs[i].split("=", 2);
            if (nameAndValue.length == 2) {
                parameters.put(URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }

    private static Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            final String[] nameAndValue = headerLine.split(":", 2);
            headers.put(nameAndValue[0], nameAndValue[1].trim());
        }
        return headers;
    }

    private static String readBody(final BufferedReader reader, final Map<String, String> requestHeaders)
            throws IOException {
        final var contentLengthHeader = requestHeaders.get(CONTENT_LENGTH_HEADER);
        if (contentLengthHeader == null) {
            return "";
        }
        final var contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public String getBody() {
        return body;
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "HttpRequest {" + "\r\n" +
                "method= " + method + "\r\n" +
                "path= " + path + "\r\n" +
                "headers= " + headers + "\r\n" +
                "body= " + body + "\r\n" +
                "parameters= " + parameters + "\r\n" +
                "}";
    }
}
