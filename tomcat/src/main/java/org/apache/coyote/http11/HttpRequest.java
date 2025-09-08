package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpMethod method;
    private final String uri;
    private final String queryString;
    private final Map<String, String> parameters;
    private final HttpCookie cookies;

    public HttpRequest(final String method, final String uri, final String queryString) {
        this(method, uri, queryString, null, null);
    }

    public HttpRequest(final String method, final String uri, final String queryString, final String body) {
        this(method, uri, queryString, body, null);
    }

    public HttpRequest(final String method, final String uri, final String queryString, final String body, final String cookieHeader) {
        this.method = HttpMethod.fromString(method);
        this.uri = uri;
        this.queryString = queryString;
        this.parameters = new HashMap<>();
        this.cookies = new HttpCookie(cookieHeader);

        parameters.putAll(parseParameters(queryString));

        if (HttpMethod.POST == this.method && body != null) {
            parameters.putAll(parseParameters(body));
        }
    }

    private Map<String, String> parseParameters(final String queryString) {
        final Map<String, String> params = new HashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return params;
        }

        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                    params.put(key, value);
                } catch (final Exception e) {
                    log.warn("Failed to parse query string: {}", queryString, e);
                }
            }
        }
        return params;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return new HashMap<>(parameters);
    }

    public HttpCookie getCookies() {
        return cookies;
    }

    public String getCookieValue(final String name) {
        return cookies.getValue(name);
    }

    public Session getSession() {
        return getSession(true);
    }

    public Session getSession(final boolean create) {
        final String sessionId = getCookieValue("JSESSIONID");
        
        if (sessionId != null) {
            final Session session = SessionManager.getInstance().findSession(sessionId);
            if (session != null) {
                return session;
            }
        }
        
        if (create) {
            return SessionManager.getInstance().createSession();
        }
        
        return null;
    }
}
