package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {
    private String method;
    private String path;
    private String fileName;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        parseRequest(bufferedReader);
    }

    public void parseRequest(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            return;
        }
        String[] requests = requestLine.split(" ");
        method = requests[0];
        path = requests[1];
        fileName = path.substring(path.lastIndexOf('/') + 1);
        headers = parseToHeaders(bufferedReader);
        body = parseToBody(bufferedReader);
    }

    private Map<String, String> parseToHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            String[] keyValues = line.split(":");
            String key = keyValues[0].trim();
            String value = keyValues[1].trim();
            headers.put(key, value);
        }
        return headers;
    }

    private String parseToBody(BufferedReader bufferedReader) throws IOException {
        if (!Objects.isNull(headers.get("Content-Length"))) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return null;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

}
