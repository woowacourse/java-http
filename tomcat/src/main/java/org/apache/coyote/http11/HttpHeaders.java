package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine = bufferedReader.readLine();

        while (!("".equals(headerLine)) && headerLine != null) {
            String[] headerLineValues = parseWithTrim(headerLine, ":");
            String headerName = headerLineValues[0];
            String headerValue = headerLineValues[1];

            headers.put(headerName, headerValue);

            headerLine = bufferedReader.readLine();
        }

        this.headers = headers;
    }

    private String[] parseWithTrim(String line, String delimiter) {
        String[] splited = line.split(delimiter);

        for (int i = 0; i < splited.length; i++) {
            splited[i] = splited[i].trim();
        }

        return splited;
    }

    public String findField(String key) {
        return this.headers.get(key);
    }

    public void setField(String key, String value) {
        this.headers.put(key, value);
    }

    public void serializeHeaders(StringBuilder stringBuilder) {
        for (Entry<String, String> headers : this.headers.entrySet()) {
            stringBuilder.append(headers.getKey() + ": " + headers.getValue() + " ").append("\r\n");
        }
    }
}
