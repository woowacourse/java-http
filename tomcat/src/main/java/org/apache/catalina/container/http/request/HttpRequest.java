package org.apache.catalina.container.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.catalina.container.exception.SocketReadException;
import org.apache.catalina.container.http.Cookie;
import org.apache.catalina.container.http.value.HttpHeader;
import org.apache.catalina.container.http.value.HttpMethod;
import org.apache.catalina.container.http.value.HttpVersion;

public class HttpRequest {

    private final RequestStartLine startLine;
    private final RequestQueryStrings queryStrings;
    private final RequestHeaders headers;
    private final RequestCookies cookies;
    private final RequestBody body;

    public HttpRequest(InputStream inputStream) {
        try {
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String startLine = readStartLine(reader);
            this.startLine = new RequestStartLine(startLine);
            this.queryStrings = new RequestQueryStrings(startLine);

            List<String> headerLines = readHeaderLines(reader);
            this.headers = new RequestHeaders(headerLines);
            this.cookies = new RequestCookies(headerLines);

            Optional<String> bodyContent = readBody(reader);
            this.body = new RequestBody(bodyContent);

        } catch (IOException e) {
            throw new SocketReadException("HTTP 요청 메세지가 올바르지 않습니다.");
        }
    }

    public boolean checkQueryStringExistence(String key) {
        return queryStrings.containKey(key);
    }

    public boolean checkHeaderExistence(String key) {
        return headers.containKey(key);
    }

    public boolean checkCookieExistence(String key) {
        return cookies.containKey(key);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public HttpVersion getVersion() {
        return startLine.getVersion();
    }

    public String getQueryString(String key) {
        return queryStrings.getValue(key);
    }

    public String getHeader(String key) {
        return headers.getValue(key.toLowerCase());
    }

    public Cookie getCookie(String key) {
        return cookies.getValue(key);
    }

    public String getBody(String key) {
        return body.getValue(key);
    }

    private String readStartLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private List<String> readHeaderLines(BufferedReader reader) throws IOException {
        //TODO: 헤더도 바이트 단위로 읽는 것을 고려  (2025-09-9, 화, 14:26)
        List<String> headerLines = new ArrayList<>();

        String line;
        while (!(line = reader.readLine()).isEmpty()) {
            headerLines.add(line);
        }
        return headerLines;
    }

    private Optional<String> readBody(BufferedReader reader) throws IOException {
        if (!checkHeaderExistence(HttpHeader.CONTENT_LENGTH.getValue())) {
            return Optional.empty();
        }

        int contentLength = Integer.parseInt(getHeader(HttpHeader.CONTENT_LENGTH.getValue()));
        char[] buffer = new char[contentLength];
        int read = reader.read(buffer, 0, contentLength);
        String bodyText = new String(buffer, 0, read);
        String decode = URLDecoder.decode(bodyText, StandardCharsets.UTF_8);
        return Optional.of(decode);
    }
}
