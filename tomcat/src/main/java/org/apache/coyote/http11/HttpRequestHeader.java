package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeader {

    private final String method;
    private String path;
    private final String version;
    private final Map<String, String> headers;

    public HttpRequestHeader(BufferedReader bufferedReader) {
        try {
            String[] headerFirstLine = bufferedReader.readLine().split(" ");
            if (headerFirstLine.length < 3) {
                throw new RuntimeException("제대로 된 요청이 아닙니다. header의 첫 줄의 값이 3개 미만입니다.");
            }
            this.method = headerFirstLine[0];
            this.path = headerFirstLine[1];
            this.version = headerFirstLine[2];
            String line;
            headers = new HashMap<>();
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                String[] requestLine = line.split(": ");
                headers.put(requestLine[0], requestLine[1]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsKey(String key) {
        return headers.containsKey(key);
    }

    public void setDefaultPath() {
        path += ".html";
    }

    public String getValue(String key) {
        return headers.get(key);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "HttpRequestHeader{" +
                "\nmethod='" + method + '\'' +
                ", \npath='" + path + '\'' +
                ", \nversion='" + version + '\'' +
                ", \nheaders=" + headers +
                '}';
    }
}
