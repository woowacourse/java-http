package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ":";
    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final String QUERY_START = "\\?";

    private String method;
    private String url;
    private String version;
    private String path;

    private final Map<String, String> queryMap = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String requestLine = bufferedReader.readLine();

        parseRequestLine(requestLine);
        parseQueryParameter();
        parseHeader(bufferedReader);
    }

    private void parseRequestLine(String requestLine) {
        String[] requestLineToken = requestLine.split(" ");

        method = requestLineToken[0];
        url = requestLineToken[1];
        version = requestLineToken[2];
    }

    private void parseHeader(BufferedReader bufferedReader) throws IOException {
        String header = bufferedReader.readLine();
        while (header != null && !header.equals("")) {
            String[] headerToken = header.split(HEADER_DELIMITER);
            String value = reconstructHeaderValue(Arrays.copyOfRange(headerToken, 1, headerToken.length));
            headers.put(headerToken[0], value);
            header = bufferedReader.readLine();
        }
    }

    private String reconstructHeaderValue(String[] headerValues) {
        StringJoiner stringJoiner = new StringJoiner(HEADER_DELIMITER);
        for (String value : headerValues) {
            stringJoiner.add(value.strip());
        }
        return stringJoiner.toString();
    }

    private void parseQueryParameter() {
        if (!url.contains("?")) {
            path = url;
            return;
        }
        String[] urlParts = url.split(QUERY_START);
        path = urlParts[0];
        String queryLine = urlParts[1];
        String[] queryList = queryLine.split(QUERY_DELIMITER);
        for (String query : queryList) {
            String[] queryParam = query.split(PARAM_DELIMITER);
            queryMap.put(queryParam[0], queryParam[1]);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }
}
