package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Request {
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> headers;
    private Map<String, String> body;
    private Map<String, String> queryParam;
    private final String httpMethod;
    private final String urlIncludeQuery;
    private final String url;
    private final String fileType;

    public Request(String requestLine, Map<String, String> headers) {
        String[] parts = requestLine.split(" ");
        if (parts.length < 2) {
            throw new IllegalArgumentException("요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.headers = new HashMap<>(headers);
        this.httpMethod = parts[0];
        this.urlIncludeQuery = parts[1];
        this.url = urlIncludeQuery.split("\\?", 2)[0];
        this.fileType = extractMainFileType(headers.get("Accept"));
    }

    public void setBody(Map<String, String> body) {
        this.body = new HashMap<>(body);
    }

    public void setQueryParam(Map<String, String> queryParam) {
        this.queryParam = new HashMap<>(queryParam);
    }

    private String extractMainFileType(String acceptHeader) {
        if (acceptHeader == null) {
            return "text/html";
        }
        String[] types = acceptHeader.split(",");
        return types[0].trim();
    }

    public boolean checkQueryParamIsEmpty() {
        return queryParam.isEmpty();
    }

    public Map<String, String> getBody() {
        return body;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrlIncludeQuery() {
        return urlIncludeQuery;
    }

    public String getUrl() {
        return url;
    }

    public String getFileType() {
        return fileType;
    }

    public int getContentLength() {
        String contentLength = headers.get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public Map<String, String> getCookie() {
        String setCookies = headers.get(SET_COOKIE);
        if (setCookies == null) {
            return new HashMap<>();
        }
        return Arrays.stream(setCookies.split(";"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));
    }
}
