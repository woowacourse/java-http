package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final String METHOD = "Method";
    private static final String URI = "URI";
    private static final String VERSION = "Version";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final String method;
    private final String uri;
    private final String version;
    private final Map<String, String> headers;
    private final RequestBody body;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestLine = extractRequestLine(bufferedReader);
        this.method = requestLine.getOrDefault(METHOD, "");
        this.uri = requestLine.getOrDefault(URI, "");
        this.version = requestLine.getOrDefault(VERSION, "");
        this.headers = extractHeaders(bufferedReader);
        this.body = extractRequestBody(bufferedReader, headers);
    }

    private Map<String, String> extractRequestLine(BufferedReader bufferedReader) throws IOException {
        Map<String, String> requestMap = new HashMap<>();
        String line = bufferedReader.readLine();
        String[] requestLine = line.split(" ");
        if (requestLine.length >= 3) {
            requestMap.put(METHOD, requestLine[0]);
            requestMap.put(URI, requestLine[1]);
            requestMap.put(VERSION, requestLine[2]);
        }
        return requestMap;
    }

    private Map<String, String> extractHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        StringBuilder header = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            header.append(line).append(System.lineSeparator());
        }
        String[] headerLines = header.toString().split(System.lineSeparator());
        for (String headerLine : headerLines) {
            String[] headerField = headerLine.split(":", 2);
            if (headerField.length == 2) {
                headerMap.put(headerField[0].trim(), headerField[1].trim());
            }
        }
        return headerMap;
    }

    public RequestBody extractRequestBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        StringBuilder body = new StringBuilder();

        int contentLength = 0;
        if (headers.containsKey(CONTENT_LENGTH)) {
            contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body.append(buffer);
        }

        return new RequestBody(body.toString());
    }

    public String getURI() {
        return uri.split("\\.")[0];
    }

    public String getHttpMethod() {
        return method;
    }

    public RequestBody getBody() {
        return body;
    }
}
