package org.apache.coyote;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestLine {

    private static final String QUERY_PARAMETER_DELIMiTER = "?";

    private String method;
    private String path;
    private Map<String, String> queryParams;
    private String protocolVersion;

    public RequestLine(String requestLine) {
        parseRequestLine(requestLine);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    private void parseRequestLine(String requestLine) {
        String firstLine = requestLine;
        String[] firstLineConditions = firstLine.split(" ");
        if (firstLineConditions.length < 3) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + firstLine);
        }
        this.method = firstLineConditions[0];
        String pathAndQuery = firstLineConditions[1];
        extractPathAndQuery(pathAndQuery);
        this.protocolVersion = firstLineConditions[2];
    }

    private void extractPathAndQuery(String pathAndQuery) {
        if (pathAndQuery.contains(QUERY_PARAMETER_DELIMiTER)) {
            int queryStartIndex = pathAndQuery.indexOf(QUERY_PARAMETER_DELIMiTER);
            path = pathAndQuery.substring(0, queryStartIndex);
            String queryString = pathAndQuery.substring(queryStartIndex + 1);

            this.queryParams = Arrays.stream(queryString.split("&"))
                    .map(param -> param.split("=", 2))
                    .collect(Collectors.toMap(
                            arr -> arr[0],
                            arr -> arr[1] //TODO: 쿼리 파라미터에서 "query" 처럼 "="을 아예 쓰지 않는 경우 추후에 고려
                    ));

        } else {
            this.path = pathAndQuery;
            this.queryParams = Map.of();
        }
    }
}
