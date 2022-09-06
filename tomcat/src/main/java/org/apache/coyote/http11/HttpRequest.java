package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String QUERY_PARAMETER_DELIMITER = "?";
    private static final String PARAMETERS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final String requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Cookies cookies;
    private boolean isCreateNewSession;
    private Session session;

    public HttpRequest(final String requestLine, final Map<String, String> headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.cookies = Cookies.from(headers.get("Cookie"));
        this.session = null;
        this.isCreateNewSession = false;
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getUriPath() {
        if (hasQueryParamsInUri()) {
            final int index = getUri().lastIndexOf(QUERY_PARAMETER_DELIMITER);
            return getUri().substring(0, index);
        }
        return getUri();
    }

    private boolean hasQueryParamsInUri() {
        return getUri().contains(QUERY_PARAMETER_DELIMITER);
    }

    private String getUri() {
        return requestLine.split(" ")[1];
    }

    public HttpMethod getHttpMethod() {
        final String methodName = requestLine.split(" ")[0];
        return HttpMethod.valueOf(methodName);
    }

    public String getRequestParam(String paramName) {
        if (hasQueryParamsInUri()) {
            return findParamValue(paramName, queryString());
        }

        if (isFormUrlencoded()) {
            return findParamValue(paramName, body);
        }

        return null;
    }

    private String queryString() {
        if (hasQueryParamsInUri()) {
            final int index = getUri().indexOf(QUERY_PARAMETER_DELIMITER);
            return getUri().substring(index + 1);
        }
        return "";
    }

    private String findParamValue(final String paramName, final String params) {
        for (String param : params.split(PARAMETERS_DELIMITER)) {
            final String key = param.split(KEY_VALUE_DELIMITER)[0];
            final String value = param.split(KEY_VALUE_DELIMITER)[1];

            if (key.equals(paramName)) {
                return value;
            }
        }

        return null;
    }

    private boolean isFormUrlencoded() {
        final String contentType = headers.get("Content-Type");
        return contentType != null && contentType.equals("application/x-www-form-urlencoded");
    }

    public String getBody() {
        return body;
    }

    public Session getSession() {
        if (hasExistentSessionIdInCookies()) {
            return SessionManager.getSession(cookies.getSessionId());
        }

        isCreateNewSession = true;
        return SessionManager.createSession();
    }

    public boolean isCreateNewSession() {
        return isCreateNewSession;
    }

    private boolean hasExistentSessionIdInCookies() {
        return cookies.hasSessionId() && SessionManager.hasSession(cookies.getSessionId());
    }
}
