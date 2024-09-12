package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String COOKIE = "Cookie";
    public static final String QUERYSTRING_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";
    public static final int QUERY_PARAMETER_KEY_INDEX = 0;
    public static final int QUERY_PARAMETER_VALUE_INDEX = 1;

    private final RequestLine requestLine;
    private final Map<String, String> header;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        log.info("requestLine : {}", line);
        this.requestLine = new RequestLine(line);
        this.header = extractHeader(bufferedReader);
        this.httpCookie = new HttpCookie(extractCookie());
        this.body = extractBody(bufferedReader);
    }

    private Map<String, String> extractHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            String[] keyValue = line.split(":");
            header.put(keyValue[0], keyValue[1].trim());
            line = bufferedReader.readLine();
        }
        return Collections.unmodifiableMap(header);
    }

    private String extractCookie() {
        if (header.containsKey(COOKIE)) {
            return header.get(COOKIE);
        }
        return "";
    }

    private String extractBody(BufferedReader bufferedReader) throws IOException {
        if (header.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(header.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return "";
    }

    public Optional<Map<String, String>> parseQueryString() {
        if (body.isEmpty()) {
            return Optional.empty();
        }
        String[] queryParameters = body.split(QUERYSTRING_SEPARATOR);
        Map<String, String> keyValue = new HashMap<>();
        for (String queryParameter : queryParameters) {
            if (!queryParameter.contains(KEY_VALUE_SEPARATOR)) {
                return Optional.empty();
            }
            String[] pair = queryParameter.split(KEY_VALUE_SEPARATOR, -1);
            keyValue.put(pair[QUERY_PARAMETER_KEY_INDEX], pair[QUERY_PARAMETER_VALUE_INDEX]);
        }
        return Optional.of(keyValue);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getCookie(String cookieName) {
        return httpCookie.getCookie(cookieName);
    }
}
