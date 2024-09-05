package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ":";
    private static final String QUERY_DELIMITER = "&";
    private static final String PARAM_DELIMITER = "=";
    private static final String QUERY_START = "\\?";

    private final Map<String, String> queryMap = new HashMap<>();
    private final RequestLine requestLine;
    private final HttpHeader header;
    private final Optional<String> body;
    private String path;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String requestLine = bufferedReader.readLine();

        this.requestLine = RequestLine.from(requestLine);
        parseQueryParameter();
        this.header = parseHeader(bufferedReader);
        this.body = parseBody(bufferedReader);
    }

    private void parseQueryParameter() {
        String url = requestLine.getUrl();
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

    private HttpHeader parseHeader(BufferedReader bufferedReader) throws IOException {
        HttpHeader header = new HttpHeader();
        String readLine = bufferedReader.readLine();
        while (readLine != null && !readLine.equals("")) {
            String[] headerToken = readLine.split(HEADER_DELIMITER);
            String value = reconstructHeaderValue(Arrays.copyOfRange(headerToken, 1, headerToken.length));
            header.addHeader(headerToken[0], value);
            readLine = bufferedReader.readLine();
        }
        return header;
    }

    private Optional<String> parseBody(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBody = new StringBuilder();

        if (!header.hasContentLength()) {
            return Optional.empty();
        }

        for (int i = 0; i < header.getContentLength(); i++) {
            stringBody.append((char) bufferedReader.read());
        }

        return Optional.of(stringBody.toString());
    }

    private String reconstructHeaderValue(String[] headerValues) {
        StringJoiner stringJoiner = new StringJoiner(HEADER_DELIMITER);
        for (String value : headerValues) {
            stringJoiner.add(value.strip());
        }
        return stringJoiner.toString();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return requestLine.getUrl();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public HttpHeader getHeaders() {
        return header;
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }

    public Optional<String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "queryMap=" + queryMap +
                ", requestLine=" + requestLine +
                ", header=" + header +
                ", body=" + body +
                ", path='" + path + '\'' +
                '}';
    }
}
