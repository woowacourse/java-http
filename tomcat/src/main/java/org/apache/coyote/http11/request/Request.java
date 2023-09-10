package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final Map<String, String> body;

    private Request(
            RequestLine requestLine,
            Map<String, String> header,
            Map<String, String> body
    ) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public static Request from(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        valid(line);
        final RequestLine requestLine = RequestLine.from(line);
        final Map<String, String> header = readHeader(bufferedReader);
        final Map<String, String> body = readBody(header, bufferedReader);
        return new Request(requestLine, header, body);
    }

    private static void valid(String line) {
        if (line == null) {
            throw new UncheckedIOException(new IOException());
        }
    }

    private static Map<String, String> readHeader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line) && line != null) {
            String[] entry = line.split(": ");
            header.put(entry[0], entry[1]);
            line = bufferedReader.readLine();
        }
        return header;
    }

    private static Map<String, String> readBody(Map<String, String> header, BufferedReader bufferedReader)
            throws IOException {
        final Map<String, String> body = new HashMap<>();
        if (!header.containsKey("Content-Length")) {
            return body;
        }
        final int contentLength = Integer.parseInt(header.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String line = new String(buffer);
        for (String entry : line.split("&")) {
            String[] bodyKeyValue = entry.split("=");
            body.put(bodyKeyValue[0], bodyKeyValue[1]);
        }
        return body;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getCookie() {
        final Map<String, String> cookie = new HashMap<>();
        if (!header.containsKey("Cookie")) {
            return cookie;
        }
        final String cookieString = header.get("Cookie");
        for (String entryString : cookieString.split("; ")) {
            String[] entry = entryString.split("=");
            cookie.put(entry[0], entry[1]);
        }
        return cookie;
    }

    public Map<String, String> getBody() {
        return body;
    }
}
