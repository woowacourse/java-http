package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final BufferedReader reader;
    private final Map<String, String> headers;

    public static HttpRequest of(BufferedReader reader) throws IOException {
        Map<String, String> headers = parseHeaders(reader);

        return new HttpRequest(reader, headers);
    }

    private HttpRequest(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        this.reader = bufferedReader;
        this.headers = headers;
    }

    public String getRequest() throws IOException {
        final var request = reader.readLine();
        if (request == null || request.isBlank()) {
            throw new IllegalArgumentException("[ERROR] request is empty: " + request);
        }

        return request.trim();
    }

    public String getRequestBody() throws IOException {
        int contentLength = getContentLength();

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public int getContentLength() {
        return toInt(headers.get("Content-Length"));
    }

    public boolean containsCookie() {
        return headers.containsKey("Cookie");
    }

    public String getCookie() {
        return headers.get("Cookie");
    }

    private static Map<String, String> parseHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while((line = reader.readLine()) != null && !line.isEmpty()) {
            int index = line.indexOf(":");
            if (index > 0) {
                String key = line.substring(0, index).trim();
                String value = line.substring(index+1).trim();
                headers.put(key, value);
            }
        }

        return headers;
    }

    private int toInt(String contentLength) {
        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException("[ERROR] invalid content length");
        }
    }
}
