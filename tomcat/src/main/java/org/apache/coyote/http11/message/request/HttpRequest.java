package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.response.ContentType;

public class HttpRequest {
    public static final int REQUEST_LINE_ELEMENT_COUNT = 3;

    //TODO: 요청라인을 전담하는 값 객체로 묶기  (2025-09-7, 일, 17:19)
    // https://github.com/woowacourse/java-http/pull/800#discussion_r2321263491
    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String version;
    private final HttpHeaders headers;
    private final HttpBody body;

    private HttpRequest(HttpMethod method, RequestUri requestUri, String version, HttpHeaders headers, HttpBody body) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    //TODO: 뎁스 줄이기. Parser 분리?  (2025-09-9, 화, 21:9)
    public static HttpRequest from(BufferedReader reader) throws IOException {
        // 요청 라인
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Empty request");
        }
        String[] requestLineTokens = requestLine.split(" ");
        if (requestLineTokens.length != REQUEST_LINE_ELEMENT_COUNT) {
            throw new IllegalArgumentException("Invalid Request Line: " + requestLine);
        }
        HttpMethod method = HttpMethod.from(requestLineTokens[0]);
        RequestUri requestUri = RequestUri.from(requestLineTokens[1]);
        String version = requestLineTokens[2];

        // 헤더 읽기
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            headerLines.add(line);
        }
        HttpHeaders headers = HttpHeaders.fromLines(headerLines);

        // 바디 읽기
        HttpBody body = HttpBody.init();
        if (headers.contains("Content-Length")) {
            int contentLength = Integer.parseInt(headers.getFirst("Content-Length"));
            char[] bodyChars = new char[contentLength];
            int read = reader.read(bodyChars, 0, contentLength);
            if (read != contentLength) {
                throw new IOException("Unexpected end of body");
            }
            body = HttpBody.from(new String(bodyChars));
        }

        return new HttpRequest(method, requestUri, version, headers, body);
    }


    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestPath() {
        return requestUri.getPath();
    }

    public Map<String, String> getQueryParams() {
        return requestUri.getQueryParams();
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Map<String, String> getBodyParams() {
        ContentType contentType = ContentType.fromMimeType(headers.getFirst("Content-Type"));
        String bodyText = body.toText();

        try {
            if (contentType == ContentType.FORM_URLENCODED) {
                return parseFormUrlEncoded(bodyText);
            }

            return Collections.emptyMap();
        } catch (Exception e) {
            throw new RuntimeException("Body parsing failed", e);
        }
    }

    public boolean hasJSessionCookie() {
        HttpCookie cookie = HttpCookie.from(headers);
        return cookie.hasJSessionId();
    }

    public String getJSessionId() {
        HttpCookie cookie = HttpCookie.from(headers);
        return cookie.getJsessionid();
    }

    private Map<String, String> parseFormUrlEncoded(String body) {
        Map<String, String> params = new HashMap<>();
        for (String pair : body.split("&")) {
            String[] keyValuePair = pair.split("=", 2);
            if (keyValuePair.length == 2) {
                params.put(URLDecoder.decode(keyValuePair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValuePair[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }
}
