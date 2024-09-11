package org.apache.catalina.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.auth.HttpCookie;

public class RequestHeader {
    public static final String ACCEPT = "Accept";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";
    public static final String TEXT_HTML = "text/html";
    public static final String COMMA = ",";
    private static final String QUERY_KEY_VALUE_DELIMITER = "=";
    public static final String SEMICOLON = ";";
    private final Map<String, String> headers;

    private final String fileType;

    public RequestHeader(Map<String, String> headers) {
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
        Map<String, String> cookie = Arrays.stream(setCookies.split(SEMICOLON))
                .map(param -> param.split(QUERY_KEY_VALUE_DELIMITER, 2))
                .filter(parts -> parts.length == 2 && parts[1] != null)
                .collect(Collectors.toMap(
                        parts -> parts[0].trim(),
                        parts -> parts[1].trim()
                ));

        return new HttpCookie(cookie);
    }
}
