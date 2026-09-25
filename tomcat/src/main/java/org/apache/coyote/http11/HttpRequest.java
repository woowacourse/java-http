package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        Map<String, String> headers = readHeaders(reader);
        String body = readRequestBody(reader, headers);
        return new HttpRequest(new RequestLine(line), headers, body);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getCookie(String name) {
        return new HttpCookie(headers.get("Cookie")).get(name);
    }

    public String getParameter(String name) {
        return parseFormParameters(body).get(name);
    }

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] keyValue = line.split(":", 2);
            if (keyValue.length == 2) {
                headers.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return headers;
    }

    private static String readRequestBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int readCount = reader.read(buffer, totalRead, contentLength - totalRead);
            if (readCount == -1) {
                break;
            }
            totalRead += readCount;
        }
        return new String(buffer, 0, totalRead);
    }

    private Map<String, String> parseFormParameters(String requestBody) {
        Map<String, String> parameters = new HashMap<>();
        for (String pair : requestBody.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }
}
