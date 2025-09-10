package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Request {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String QUERY_PARAMETER_DELIMiTER = "?";

    private String[] firstLineConditions;
    private final Map<String, String> header = new HashMap<>();
    private String path;
    private Map<String, String> queryParams;
    private Http11Cookie cookies;
    private String body;

    public Http11Request(InputStream inputStream) throws IOException {
        extractHeaderAndBody(inputStream);
    }

    public String getPath() {
        return path;
    }

    public String getRequestMethod() {
        String requestMethod = firstLineConditions[0];

        return requestMethod;
    }

    public String getBody() {
        return body;
    }

    public Http11Cookie getCookies() {
        return cookies;
    }

    private void extractHeaderAndBody(InputStream inputStream) throws IOException {
        final StringBuilder requestHeader = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, DEFAULT_CHARSET));

        String line;
        while (((line = bufferedReader.readLine()) != null) && (!line.isEmpty())) {
            requestHeader.append(line).append("\r\n");
        }
        String header = requestHeader.toString();

        String[] requestConditions = header.split("\r\n");
        if (requestConditions.length < 1) {
            throw new IOException("Invalid HTTP request: empty header");
        }

        extractFirstLineConditions(requestConditions);
        extractHeaders(requestConditions);
        int contentLength = getContentLength();
        extractCookies();
        extractBody(bufferedReader, contentLength);
        extractUri();
    }

    private void extractCookies() {
        this.cookies = new Http11Cookie(header.get("cookie"));
    }

    private int getContentLength() {
        String contentLength = this.header.get("content-length");
        if (contentLength == null) {
            return 0;
        }

        return Integer.parseInt(contentLength);
    }

    private void extractHeaders(String[] requestConditions) {
        for (int i = 1; i < requestConditions.length; i++) {
            String[] set = requestConditions[i].split(":", 2);
            this.header.put(set[0].trim().toLowerCase(), set[1].trim());
        }
    }

    private void extractBody(BufferedReader br, int contentLength) throws IOException {
        if (contentLength <= 0) {
            this.body = "";
            return;
        }
        char[] buf = new char[contentLength];
        int off = 0;
        while (off < contentLength) {
            int r = br.read(buf, off, contentLength - off);
            if (r == -1) {
                throw new IOException("Unexpected EOF while reading body");
            }
            off += r;
        }
        this.body = new String(buf, 0, off);
    }

    private void extractFirstLineConditions(String[] requestConditions) throws IOException {
        String firstLine = requestConditions[0];
        firstLineConditions = firstLine.split(" ");
        if (firstLineConditions.length < 3) {
            throw new IOException("Invalid HTTP request line: " + firstLine);
        }
    }

    private void extractUri() {
        String uri = firstLineConditions[1];

        if (uri.contains(QUERY_PARAMETER_DELIMiTER)) {
            int queryStartIndex = uri.indexOf(QUERY_PARAMETER_DELIMiTER);
            path = uri.substring(0, queryStartIndex);
            String queryString = uri.substring(queryStartIndex + 1);

            queryParams = Arrays.stream(queryString.split("&"))
                    .map(param -> param.split("=", 2))
                    .collect(Collectors.toMap(
                            arr -> arr[0],
                            arr -> arr[1] //TODO: 쿼리 파라미터에서 "query" 처럼 "="을 아예 쓰지 않는 경우 추후에 고려
                    ));

        } else {
            path = uri;
            queryParams = Map.of();
        }
    }
}
