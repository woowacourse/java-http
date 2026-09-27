package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;
    private final Map<String, String> parameters;
    private final SessionManager sessionManager;
    private Session session;
    private boolean newSession;

    public HttpRequest(final InputStream inputStream) throws IOException {
        this(inputStream, SessionManager.getInstance());
    }

    public HttpRequest(final InputStream inputStream, final SessionManager sessionManager) throws IOException {
        this.requestLine = new RequestLine(inputStream);
        this.headers = new RequestHeaders(inputStream);
        this.body = new RequestBody(inputStream, headers);
        this.parameters = parseParameters(requestLine.getTarget(), body.getContent());
        this.sessionManager = sessionManager;
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getHeaders() {
        return headers;
    }

    public RequestBody getBody() {
        return body;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public boolean hasParameters() {
        return !parameters.isEmpty();
    }

    public Session getSession() throws IOException {
        return getSession(true);
    }

    public Session getSession(final boolean create) throws IOException {
        if (session != null) {
            return session;
        }

        final HttpCookie cookies = new HttpCookie(headers.get("Cookie"));
        final String sessionId = cookies.get("JSESSIONID");
        if (sessionId != null) {
            session = sessionManager.findSession(sessionId);
        }

        if (session == null && create) {
            session = sessionManager.createSession();
            newSession = true;
        }
        return session;
    }

    public boolean isNewSession() {
        return newSession;
    }

    private Map<String, String> parseParameters(final String target, final String body) {
        final Map<String, String> parameters = new LinkedHashMap<>();
        parseParameterString(getQueryString(target), parameters);
        parseParameterString(body, parameters);
        return parameters;
    }

    private String getQueryString(final String target) {
        final int queryStart = target.indexOf('?');
        if (queryStart == -1) {
            return null;
        }
        return target.substring(queryStart + 1);
    }

    private void parseParameterString(final String parameterString, final Map<String, String> parameters) {
        if (parameterString == null || parameterString.isEmpty()) {
            return;
        }

        for (String parameter : parameterString.split("&")) {
            final int separator = parameter.indexOf('=');
            final String encodedName = separator == -1 ? parameter : parameter.substring(0, separator);
            final String encodedValue = separator == -1 ? "" : parameter.substring(separator + 1);
            parameters.putIfAbsent(decode(encodedName), decode(encodedValue));
        }
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
