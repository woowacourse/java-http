package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private final String method;
    private final String path;
    private final String httpVersion;
    private final Map<String, String> queryParams;

    public RequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new BadRequestException("잘못된 요청 라인입니다.");
        }

        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new BadRequestException("잘못된 요청 라인입니다.");
        }

        this.method = tokens[0];
        this.httpVersion = tokens[2];

        String uri = tokens[1];
        int queryIndex = uri.indexOf("?");
        if (queryIndex >= 0) {
            this.path = uri.substring(0, queryIndex);
            this.queryParams = HttpParamParser.parseKeyValuePairs(uri.substring(queryIndex + 1), "&");
        } else {
            this.path = uri;
            this.queryParams = new HashMap<>();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }
}
