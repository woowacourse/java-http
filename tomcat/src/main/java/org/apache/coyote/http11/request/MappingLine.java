package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class MappingLine { // GET /endPoint HTTP/1.1

    private final String requestMapping; // GET //-
    private final String url; // /endPoint
    private Map<String, String> parameters;
    private final String protocol; // HTTP/1.1

    private MappingLine(String requestMapping, String url, String protocol) {
        this.requestMapping = requestMapping;
        this.url = url;
        this.protocol = protocol;
    }

    public MappingLine(String[] parts) {
        this(parts[0], parts[1], parts[2]);
    }

    public MappingLine(BufferedReader bufferedReader) throws IOException {
        String[] parsedRequestLine = parseRequestLine(bufferedReader);

        this.requestMapping = parsedRequestLine[0];
        String fullUrl = parsedRequestLine[1];
        int idx = fullUrl.indexOf("?");
        if (idx != -1) { // TODO 2025. 9. 7. 22:42: if-else 리펙터링 하기
            this.url = fullUrl.substring(0, idx);
            this.parameters = parseParameters(fullUrl.substring(idx + 1));
        }  else {
            this.url = fullUrl;
            this.parameters = Map.of();
        }

        this.protocol = parsedRequestLine[2];
    }

    private String[] parseRequestLine(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("request is Empty");
        }

        String[] parts = requestLine.split(" ", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException();
        }
        return parts;
    }

    private Map<String, String> parseParameters(String query) {
        return Arrays.stream(query.split("&"))
                .map(s -> s.split("=", 2))
                .collect(Collectors.toMap(
                        kv -> kv[0],
                        kv -> kv.length > 1 ? kv[1] : ""
                ));
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getRequestMapping() {
        return requestMapping;
    }

    public String getUrl() {
        return url;
    }
}
