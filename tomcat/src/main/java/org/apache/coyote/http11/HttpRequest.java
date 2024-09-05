package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.exception.UnexpectQueryParamException;
import org.apache.coyote.exception.UnexpectedHeaderException;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final String uri;
    private final String path;
    private final Map<String, String> params = new HashMap<>();
    private final String protocol;
    private final Map<String, String[]> headers = new HashMap<>();
    private final String body;
    private final Cookies cookie;

    public HttpRequest(
            String method, String uri, String path, String[] paramStrings, String protocol, String headers, String body
    ) {
        this.method = HttpMethod.valueOf(method);
        this.uri = uri;
        this.path = path;
        mapQueryParams(paramStrings);
        this.protocol = protocol;
        mapHeaders(headers);
        this.cookie = new Cookies();
        parseCookies();
        this.body = body;
    }

    private void mapQueryParams(String[] pairs) {
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");

            String key = keyValue[0];
            String value = "";

            if (keyValue.length > 1) {
                value = keyValue[1];
            }
            params.put(key, value);
        }
    }

    private void mapHeaders(String headerString) {
        String[] headerLines = headerString.split("\r\n");

        for (String headerLine : headerLines) {
            String[] pair = headerLine.split(": ");
            String[] headerValues = pair[1].split(";");

            headers.put(
                    pair[0],
                    Arrays.stream(headerValues)
                            .map(String::trim).toArray(String[]::new)
            );
        }
    }

    private void parseCookies() {
        String[] cookies = headers.get("Cookie");
        if (cookies == null) {
            throw new UnexpectedHeaderException("Cookie");
        }
        for (String cookieLine : cookies) {
            cookie.addCookie(cookieLine);
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
            throw new UnexpectQueryParamException();
        }
        return paramValue;
    }

    public String[] getHeaders(String headerKey) {
        String[] headerValues = headers.get(headerKey);
        if (headerValues == null) {
            throw new UnexpectedHeaderException(headerKey);
        }
        return headerValues;
    }

    @Override
    public String toString() {
        return "HttpRequest{" + "body='" + body + '\'' + ", method=" + method + ", uri='" + uri + '\'' + ", path='"
                + path + '\'' + ", params=" + params + ", protocol='" + protocol + '\'' + ", headers='" + headers + '\''
                + '}';
    }
}
