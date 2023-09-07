package org.apache.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class HttpRequest {

    private final String httpMethod;
    private final String uri;
    private final String httpVersion;
    private final HttpHeaders httpHeaders;
    private final Map<String, String> queryParameter;
    private final Map<String, String> requestBody;

    public HttpRequest(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        final String[] parts = requestLine.split(" ");
        this.httpMethod = parts[0];
        this.uri = parts[1];
        this.httpVersion = parts[2];
        this.httpHeaders = new HttpHeaders(parsingRequestHeaders(reader));
        this.requestBody = parsingRequestBody(httpHeaders, reader);
        this.queryParameter = parsingQueryParameter(this.uri);
    }

    private Map<String, String> parsingRequestHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> requests = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final int colonIndex = line.indexOf(": ");
            final String key = line.substring(0, colonIndex);
            final String value = line.substring(colonIndex + 2);
            requests.put(key, value);
        }
        return requests;
    }

    private Map<String, String> parsingRequestBody(final HttpHeaders httpHeaders, final BufferedReader reader) throws IOException {
        final Map<String, String> requestBodies = new HashMap<>();
        if (httpHeaders.containsHeader(HttpHeaders.CONTENT_LENGTH)) {
            final int contentLength = Integer.parseInt(httpHeaders.getHeaderValue(HttpHeaders.CONTENT_LENGTH));
            final char[] buffer = new char[contentLength];
            reader.read(buffer, 0, contentLength);
            final String requestBody = new String(buffer);
            Arrays.stream(requestBody.split("&")).forEach(query -> {
                final String[] keyValue = query.split("=");
                final String decodedValue = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                requestBodies.put(keyValue[0], decodedValue);
            });
        }
        return requestBodies;
    }

    private Map<String, String> parsingQueryParameter(final String path) {
        final Map<String, String> queryStrings = new HashMap<>();
        if (path.contains("?")) {
            final String queryString = path.substring(path.indexOf("?") + 1);
            Arrays.stream(queryString.split("&")).forEach(query -> {
                String[] keyValue = query.split("=");
                queryStrings.put(keyValue[0], keyValue[1]);
            });
        }

        return queryStrings;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getURI() {
        return this.uri;
    }

    public String getRequestURI() {
        if (this.uri.contains("?")) {
            return this.uri.substring(0, this.uri.indexOf("?"));
        }

        return this.uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }
}
