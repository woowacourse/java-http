package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.exception.UnexpectedQueryParamException;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.util.Symbol;

public class HttpRequest {

    private static final String COOKIE_HEADER_KEY = "Cookie";
    private final HttpMethod method;
    private final String uri;
    private final String path;
    private final Map<String, String> params = new HashMap<>();
    private final String protocol;
    private final Map<String, List<String>> headers = new HashMap<>();
    private final String body;
    private final Cookies cookie;

    public HttpRequest(
            String method,
            String uri,
            String path,
            String[] paramStrings,
            String protocol,
            String[] headerLines,
            String body
    ) {
        this.method = HttpMethod.valueOf(method);
        this.uri = uri;
        this.path = path;
        mapQueryParams(paramStrings);
        this.protocol = protocol;
        mapHeaders(headerLines);
        this.cookie = new Cookies();
        parseCookies();
        this.body = body;
    }

    private void mapQueryParams(String[] pairs) {
        for (String pair : pairs) {
            String[] keyValue = pair.split(Symbol.QUERY_PARAM_DELIMITER);

            String key = keyValue[0];
            String value = Symbol.EMPTY;

            if (keyValue.length > 1) {
                value = keyValue[1];
            }
            params.put(key, value);
        }
    }

    private void mapHeaders(String[] headerLines) {
        for (String headerLine : headerLines) {
            String[] pair = headerLine.split(Symbol.COLON);
            List<String> headerValues = Arrays.stream(pair[1].split(Symbol.SEMICOLON))
                    .toList();

            headers.put(
                    pair[0],
                    headerValues.stream()
                            .map(String::trim)
                            .toList()
            );
        }
    }

    private void parseCookies() {
        List<String> cookies = headers.get(COOKIE_HEADER_KEY);
        if (cookies == null) {
            return;
        }
        for (String cookiePair : cookies) {
            String[] pairs = cookiePair.split(Symbol.QUERY_PARAM_DELIMITER);
            String name = pairs[0];
            String value = pairs[1];
            cookie.setCookie(name, value);
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }

    public String getParams(String paramKey) {
        String paramValue = params.get(paramKey);
        if (paramValue == null) {
            throw new UnexpectedQueryParamException();
        }
        return paramValue;
    }

    public Cookies getCookies() {
        return cookie;
    }

    @Override
    public String toString() {
        return "HttpRequest{" + "body='" + body + '\'' + ", method=" + method + ", uri='" + uri + '\'' + ", path='"
                + path + '\'' + ", params=" + params + ", protocol='" + protocol + '\'' + ", headers='" + headers + '\''
                + '}';
    }
}
