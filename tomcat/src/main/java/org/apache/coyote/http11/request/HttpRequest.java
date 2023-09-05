package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.utils.Parser.parseFormData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final String LINE_SEPARATOR = "\r\n";

    final Method method;
    final String path;
    final Map<String, String> queryParams;
    final Map<String, String> headers;
    final Map<String, String> cookies;
    String body;

    public HttpRequest(final Method method,
                       final String path,
                       final Map<String, String> queryParams,
                       final Map<String, String> headers,
                       final Map<String, String> cookies
    ) {
        this.method = method;
        this.path = path;
        this.queryParams = queryParams;
        this.headers = headers;
        this.cookies = cookies;
    }

    public static HttpRequest from(final String request) {
        final List<String> lines = new ArrayList<>(List.of(request.split(LINE_SEPARATOR)));

        final String[] requestUri = lines.remove(0).split(" ");
        final String method = requestUri[0];
        final String uri = requestUri[1];

        final String[] uris = uri.split("\\?");
        final String path = uris[0];

        String queryString = "";
        if (uris.length > 1) {
            queryString = uris[1];
        }
        final Map<String, String> queryParams = parseFormData(queryString);
        final Map<String, String> headers = parseHeaders(lines);

        final String cookieFields = headers.remove("Cookie");
        final Map<String, String> cookies = parseCookies(cookieFields);

        return new HttpRequest(
                Method.getMethod(method),
                path,
                queryParams,
                headers,
                cookies
        );
    }

    private static Map<String, String> parseHeaders(final List<String> lines) {
        final Map<String, String> headers = new HashMap<>();
        for (final String line : lines) {
            if ("".equals(line)) {
                break;
            }
            final String[] header = line.split(": ", 2);
            headers.put(header[0], header[1]);
        }
        return headers;
    }

    private static Map<String, String> parseCookies(final String cookieFields) {
        final Map<String, String> cookies = new HashMap<>();
        if (cookieFields == null) {
            return cookies;
        }

        for (final String field : cookieFields.split("; ")) {
            final String[] cookie = field.split("=", 2);
            cookies.put(cookie[0], cookie[1]);
        }
        return cookies;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public boolean isQueryParamExist(final String... parameterNames) {
        boolean isExist = true;
        for (final String parameterName : parameterNames) {
            isExist = isExist && queryParams.containsKey(parameterName);
        }
        return isExist;
    }

    public String getQueryParam(final String parameterName) {
        return queryParams.get(parameterName);
    }

    public String getHeader(final String headerName) {
        return headers.get(headerName);
    }

    public boolean isCookieExist(final String cookieName) {
        return cookies.containsKey(cookieName);
    }

    public String getCookie(final String cookieName) {
        return cookies.get(cookieName);
    }

    public String getBody() {
        return body;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
