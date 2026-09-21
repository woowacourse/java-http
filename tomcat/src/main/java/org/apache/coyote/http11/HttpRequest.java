package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest (RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        Map<String, String> headers = new LinkedHashMap<>();
        readHeaders(bufferedReader, headers);

        final int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        String requestBody = "";
        requestBody = readBody(bufferedReader, contentLength, requestBody);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static String readBody(BufferedReader bufferedReader, int contentLength, String requestBody) throws IOException {
        if(contentLength > 0) {
            char[] buffer = new char[contentLength];
            int totalRead = 0;
            readBodyLines(bufferedReader, contentLength, totalRead, buffer);
            requestBody = new String(buffer);
        }
        return requestBody;
    }

    private static void readBodyLines(BufferedReader bufferedReader, int contentLength, int totalRead, char[] buffer) throws IOException {
        while (totalRead < contentLength) {
            int count = bufferedReader.read(buffer, totalRead, contentLength - totalRead);
            if (count == -1) throw new IOException("예상보다 짧음");
            totalRead += count;
        }
    }

    private static void readHeaders(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) break;
            String[] parts = line.split(":", 2);
            headers.put(parts[0], parts[1].trim());
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public Uri getUri() {
        return requestLine.getUri();
    }

    public String getHeader(String name) {
        if(headers.containsKey(name)) {
            return headers.get(name);
        }
        return "";
    }

    public String getBody() {
        return body;
    }

}
