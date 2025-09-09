package org.apache.coyote.http11;

import com.techcourse.exception.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams;
    private final HttpHeaders headers;
    private final String body;
    private final HttpCookie cookies;

    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new BadRequestException("잘못된 요청 라인입니다.");
        }
        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new BadRequestException("잘못된 요청 라인입니다.");
        }
        this.method = tokens[0];
        String uri = tokens[1];
        int queryIndex = uri.indexOf("?");
        if (queryIndex >= 0) {
            this.path = uri.substring(0, queryIndex);
            this.queryParams = HttpParamParser.parseKeyValuePairs(uri.substring(queryIndex + 1), "&");
        } else {
            this.path = uri;
            this.queryParams = new HashMap<>();
        }

        this.headers = HttpHeaders.parse(reader);

        if ("POST".equalsIgnoreCase(method)) {
            String contentLengthValue = headers.get("Content-Length");
            int contentLength = Integer.parseInt(contentLengthValue);
            char[] buffer = new char[contentLength];
            int read = reader.read(buffer, 0, contentLength);
            this.body = new String(buffer, 0, read);

            if ("application/x-www-form-urlencoded".equalsIgnoreCase(headers.get("Content-Type"))) {
                queryParams.putAll(HttpParamParser.parseKeyValuePairs(body, "&"));
            }
        } else {
            this.body = null;
        }

        this.cookies = new HttpCookie(headers.get("Cookie"));
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryParam(String key) {
        return queryParams.get(key);
    }

    public HttpCookie getCookies() {
        return cookies;
    }
}
