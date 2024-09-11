package org.apache.catalina.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.auth.HttpCookie;

public class Request {
    public static final String ACCEPT = "Accept";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    public static final String TEXT_HTML = "text/html";
    public static final String COMMA = ",";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private Map<String, String> body = new HashMap<>();
    private final String fileType;

    public Request(String requestLine, Map<String, String> headers) {
        this.requestLine = new RequestLine(requestLine);
        this.headers = new HashMap<>(headers);
        this.fileType = extractMainFileType(headers.get(ACCEPT));
    }

    private String extractMainFileType(String acceptHeader) {
        if (acceptHeader == null) {
            return TEXT_HTML;
        }
        String[] types = acceptHeader.split(COMMA);
        return types[0].trim();
    }

    public boolean checkQueryParamIsEmpty() {
        return requestLine.checkQueryParamIsEmpty();
    }

    public void setBody(Map<String, String> body) {
        this.body = new HashMap<>(body);
    }

    public String getHttpMethod() {
        return requestLine.getHttpMethod().name();
    }

    public String getPathWithoutQuery() {
        return requestLine.getPathWithoutQuery();
    }

    public Map<String, String> getQueryParam() {
        return requestLine.getQueryParam();
    }

    public Map<String, String> getBody() {
        return body;
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

    public HttpCookie getCookie() {
        String setCookies = headers.get(COOKIE);
        if (setCookies == null) {
            return new HttpCookie(new HashMap<>());
        }
        Map<String, String> cookie = Arrays.stream(setCookies.split(";"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));

        return new HttpCookie(cookie);
    }
}
