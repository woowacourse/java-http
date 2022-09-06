package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String QUERY_STRING_DELIMITER = "?";

    private final String requestLine;
    private final Map<String, String> headers;
    private final String body;
    private Session session;

    public HttpRequest(final String requestLine, final Map<String, String> headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.session = null;
    }

    public boolean hasHeader(String name) {
        return headers.containsKey(name);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getUriPath() {
        if (hasQueryParamsInUri()) {
            final int index = getUri().lastIndexOf('?');
            return getUri().substring(0, index);
        }
        return getUri();
    }

    private boolean hasQueryParamsInUri() {
        return getUri().contains(QUERY_STRING_DELIMITER);
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
            final int index = getUri().indexOf("?");
            return getUri().substring(index + 1);
        }
        return "";
    }

    private String findParamValue(final String paramName, final String params) {
        for (String param : params.split("&")) {
            final String key = param.split("=")[0];
            final String value = param.split("=")[1];

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
        if (session != null) {
            return session;
        }

        final String cookie = headers.get("Cookie");

        if (cookie != null) {
            final String sessionId = cookie.split("=")[1];
            if (SessionManager.getSession(sessionId).isPresent()) {
                session = SessionManager.getSession(sessionId).get();
                return session;
            }
        }

        session = SessionManager.createSession();
        return session;
    }

    public boolean hasSession() {
        return session != null;
    }
}
