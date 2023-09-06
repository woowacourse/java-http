package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.utils.Constant.COOKIES_DELIMITER;
import static org.apache.coyote.http11.utils.Constant.COOKIE_DELIMITER;
import static org.apache.coyote.http11.utils.Constant.EMPTY;
import static org.apache.coyote.http11.utils.Constant.HEADER_DELIMITER;
import static org.apache.coyote.http11.utils.Constant.LINE_SEPARATOR;
import static org.apache.coyote.http11.utils.Parser.parseFormData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final String QUERY_STRING_DELIMITER = "\\?";
    private static final String SPLIT_DELIMITER = " ";
    private static final int REQUEST_LINE_INDEX = 0;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PATH_INDEX = 0;
    private static final int QUERY_STRING_INDEX = 1;
    private static final String HEADER_COOKIE = "Cookie";
    private static final int SPLIT_LIMIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
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

        final String[] requestUri = lines.remove(REQUEST_LINE_INDEX).split(SPLIT_DELIMITER);
        final String method = requestUri[METHOD_INDEX];
        final String uri = requestUri[URI_INDEX];

        final String[] uris = uri.split(QUERY_STRING_DELIMITER);
        final String path = uris[PATH_INDEX];

        String queryString = EMPTY;
        if (uris.length > 1) {
            queryString = uris[QUERY_STRING_INDEX];
        }
        final Map<String, String> queryParams = parseFormData(queryString);
        final Map<String, String> headers = parseHeaders(lines);

        final String cookieFields = headers.remove(HEADER_COOKIE);
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
            if (EMPTY.equals(line)) {
                break;
            }
            final String[] header = line.split(HEADER_DELIMITER, SPLIT_LIMIT_SIZE);
            headers.put(header[KEY_INDEX], header[VALUE_INDEX]);
        }
        return headers;
    }

    private static Map<String, String> parseCookies(final String cookieFields) {
        final Map<String, String> cookies = new HashMap<>();
        if (cookieFields == null) {
            return cookies;
        }

        for (final String field : cookieFields.split(COOKIES_DELIMITER)) {
            final String[] cookie = field.split(COOKIE_DELIMITER, SPLIT_LIMIT_SIZE);
            cookies.put(cookie[KEY_INDEX], cookie[VALUE_INDEX]);
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

    public String getHeader(final RequestHeader header) {
        return headers.get(header.getName());
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
