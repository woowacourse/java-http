package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;
    private final HttpCookie cookies;
    private Session session;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        this.requestLine = new RequestLine(reader.readLine());
        this.headers = HttpHeaders.parse(reader);
        this.body = parseBodyIfNecessary(reader);
        this.cookies = new HttpCookie(headers.get("Cookie"));
    }

    private String parseBodyIfNecessary(BufferedReader reader) throws IOException {
        if (!"POST".equalsIgnoreCase(requestLine.getMethod())) {
            return null;
        }
        String contentLengthValue = headers.get("Content-Length");
        if (contentLengthValue == null) {
            return null;
        }
        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];
        int read = reader.read(buffer);
        if (read < 0) {
            throw new BadRequestException("요청 본문을 읽을 수 없습니다.");
        }
        String body = new String(buffer, 0, read);
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(headers.get("Content-Type"))) {
            requestLine.getQueryParams().putAll(HttpParamParser.parseKeyValuePairs(body, "&"));
        }
        return body;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public boolean isGetMethod() {
        return "GET".equalsIgnoreCase(requestLine.getMethod());
    }

    public boolean isPostMethod() {
        return "POST".equalsIgnoreCase(requestLine.getMethod());
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getQueryParam(String key) {
        return requestLine.getQueryParam(key);
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public String getBody() {
        return body;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
