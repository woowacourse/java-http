package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StartLine {

    private final String httpMethod;
    private final String requestUri;
    private final Map<String, String> queryParams;

    public StartLine(String startLine) {
        String[] splitLine = startLine.split(" ");
        this.httpMethod = splitLine[0];

        int index = splitLine[1].indexOf("?");
        if (index == -1) {
            this.requestUri = startLine;
            this.queryParams = new HashMap<>();
            return;
        }

        String requestUri = splitLine[1].substring(0, index);
        String queryString = splitLine[1].substring(index + 1);

        this.requestUri = requestUri;
        this.queryParams = toQueryMap(queryString);
    }

    private Map<String, String> toQueryMap(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
