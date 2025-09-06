package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11Request {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String QUERY_PARAMETER_DELIMiTER = "?";

    private InputStream inputStream;
    private String header;
    private String firstLine;
    private String[] firstLineConditions;
    private String path;
    private Map<String, String> queryParams;

    public Http11Request(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
        extractHeader();
    }

    public String getPath() {
        return path;
    }

    public String getQueryParamValue(String queryParam) {
        return queryParams.get(queryParam);
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getRequestMethod() {
        String requestMethod = firstLineConditions[0];

        return requestMethod;
    }

    private void extractHeader() throws IOException {
        final StringBuilder requestHeader = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream, DEFAULT_CHARSET));

        String line;
        while (((line = bufferedReader.readLine()) != null) && (!line.isEmpty())) {
            requestHeader.append(line).append("\r\n");
        }
        header = requestHeader.toString();

        String[] requestConditions = header.split("\r\n");
        if (requestConditions.length < 1) {
            throw new IOException("Invalid HTTP request: empty header");
        }

        firstLine = requestConditions[0];
        firstLineConditions = firstLine.split(" ");
        if (firstLineConditions.length < 3) {
            throw new IOException("Invalid HTTP request line: " + firstLine);
        }

        extractUri();
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
