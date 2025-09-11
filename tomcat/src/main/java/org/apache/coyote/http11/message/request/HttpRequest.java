package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.message.HttpBody;
import org.apache.coyote.http11.message.HttpCookie;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.parser.HttpBodyParser;
import org.apache.coyote.http11.message.parser.Parser;
import org.apache.coyote.http11.message.response.ContentType;

public class HttpRequest {
    public static final int REQUEST_LINE_ELEMENT_COUNT = 3;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    private HttpRequest(RequestLine requestLine, HttpHeaders headers, HttpBody body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader reader,
                                   Parser<RequestLine> requestLineParser,
                                   Parser<HttpHeaders> httpHeadersParser) throws IOException {
        // 요청 라인
        RequestLine requestLine = requestLineParser.parse(reader);
        // 헤더
        HttpHeaders headers = httpHeadersParser.parse(reader);
        // 바디 읽기
        HttpBodyParser httpBodyParser = new HttpBodyParser(headers);
        HttpBody body = httpBodyParser.parse(reader);

        return new HttpRequest(requestLine, headers, body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getRequestPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getQueryParams() {
        return requestLine.getQueryParams();
    }

    public String getVersion() {
        return requestLine.getVersion();
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
