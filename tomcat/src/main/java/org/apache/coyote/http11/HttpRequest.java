package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String QUERY_PARAM_DELIMETER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMETER = "=";
    private final HttpMethod method;
    private final String requestUrl;
    private final HttpHeader header;
    private final HttpCookie httpCookie;
    private final Map<String, String> body;

    public HttpRequest(HttpHeader header, HttpMethod method, String requestUrl, HttpCookie httpCookie,
                       Map<String, String> body) {
        this.header = header;
        this.method = method;
        this.requestUrl = requestUrl;
        this.httpCookie = httpCookie;
        this.body = body;
    }

    public static HttpRequest of(List<String> header, String rawBody) {
        String[] requestLine = header.getFirst().split(" ");
        HttpMethod method = HttpMethod.valueOf(requestLine[0]);
        String requestUrl = requestLine[1];

        Map<String, String> body = parseBody(rawBody);
        HttpHeader httpHeader = HttpHeader.from(header);
        HttpCookie httpCookie = HttpCookie.from(httpHeader.getHeader(HttpHeaderName.COOKIE));

        return new HttpRequest(httpHeader, method, requestUrl, httpCookie, body);
    }

    private static Map<String, String> parseBody(String rawBody) {
        if (rawBody.isEmpty()) {
            return new HashMap<>();
        }

        return Arrays.stream(rawBody.split(QUERY_PARAM_DELIMETER))
                .collect(Collectors.toMap(
                        s -> s.split(QUERY_PARAM_VALUE_DELIMETER)[0],
                        s -> s.split(QUERY_PARAM_VALUE_DELIMETER)[1])
                );
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getBody(String key) {
        return body.get(key);
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }
}
