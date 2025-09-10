package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

public class HttpRequest {

    private final RequestMethod requestMethod;
    private final String requestUrl;
    private final String httpVersion;
    private final RequestHeader requestHeader;
    private final Map<String, String> parameters;
    private final SessionManager sessionManager;

    public HttpRequest(
            final RequestMethod requestMethod,
            final String requestUrl,
            final String httpVersion,
            final RequestHeader requestHeader,
            final Map<String, String> parameters,
            final SessionManager sessionManager
    ) {
        this.requestMethod = requestMethod;
        this.requestUrl = requestUrl;
        this.httpVersion = httpVersion;
        this.requestHeader = requestHeader;
        this.parameters = parameters;
        this.sessionManager = sessionManager;
    }

    public static HttpRequest from(final String rawHttpRequest) {
        String[] parts = rawHttpRequest.split("\r\n\r\n", 2);

        String headerPart = parts[0];
        String bodyPart = parts.length > 1 ? parts[1] : "";

        String[] headerLines = headerPart.split("\r\n");
        String[] requestLine = headerLines[0].trim().split(" ");

        RequestMethod requestMethod = RequestMethod.valueOf(requestLine[0]);
        String requestUrl = requestLine[1];
        String requestHttpVersion = requestLine[2];

        Map<String, String> parameters = new HashMap<>();
        if (requestUrl.contains("?")) {
            int index = requestUrl.indexOf("?");
            String queryStrings = requestUrl.substring(index + 1);
            putParameters(queryStrings, parameters);
            requestUrl = requestUrl.substring(0, index);
        }

        RequestHeader requestHeader = RequestHeader.from(headerPart);
        if (requestHeader.getHeader("Content-Type") != null && requestHeader.getHeader("Content-Type")
                .equals("application/x-www-form-urlencoded")) {
            putParameters(bodyPart, parameters);
        }

        return new HttpRequest(
                requestMethod,
                requestUrl,
                requestHttpVersion,
                requestHeader,
                parameters,
                new SessionManager()
        );
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHeader(String name) {
        return requestHeader.getHeader(name);
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public Session getSession(boolean create) {
        Cookie sessionCookie = requestHeader.getSessionCookie();
        if (sessionCookie != null) {
            Session session = sessionManager.findSession(sessionCookie.getValue());
            if (session != null) {
                return session;
            }
        }

        if (create) {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        }

        return null;
    }

    private static void putParameters(String queryStrings, Map<String, String> parameters) {
        for (String queryString : queryStrings.split("&")) {
            String[] strings = queryString.split("=", 2);
            String key = URLDecoder.decode(strings[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(strings[1], StandardCharsets.UTF_8);
            parameters.put(key, value);
        }
    }
}
