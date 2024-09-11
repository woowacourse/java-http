package org.apache.catalina.controller.http.request;

import static java.util.stream.Collectors.toMap;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.controller.http.Cookie;
import org.apache.catalina.controller.http.HttpVersion;
import org.apache.catalina.controller.http.Session;
import org.apache.catalina.controller.http.SessionManager;

public class HttpRequest {

    private static final String SP = " ";

    private RequestLine requestLine;
    private Map<String, String> headers;
    private String body = "";

    public HttpRequest(String request) {
        this.requestLine = new RequestLine(parseHttpMethod(request), parsePath(request), parseVersion(request));
        this.headers = parseHeaders(request);
        this.body = parseBody(request);
    }

    private String parseHttpMethod(String request) {
        String startLine = parseStartLine(request);
        return startLine.split(SP)[0];
    }

    private String parsePath(String request) {
        String[] startLine = parseStartLine(request).split(SP);
        return startLine[1];
    }

    private String parseVersion(String request) {
        String[] startLine = parseStartLine(request).split(SP);
        return startLine[2];
    }

    private String parseStartLine(String request) {
        return request.split("\r\n")[0];
    }

    private Map<String, String> parseHeaders(String request) {
        String[] startLineAndHeaders = request.split("\r\n\r\n")[0].split("\r\n");
        String[] headers = Arrays.copyOfRange(startLineAndHeaders, 1, startLineAndHeaders.length);
        return Arrays.stream(headers)
                .map(header -> header.split(": ", 2))
                .collect(toMap(header -> header[0], header -> header[1]));
    }

    private String parseBody(String request) {
        String[] split = request.split("\r\n\r\n");
        if (split.length > 1) {
            return split[1];
        }
        return "";
    }

    public Map<String, String[]> getQueryParameters() {
        return requestLine.getQueryParameters();
    }

    public String getRequestURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(getScheme()).append("://").append(getServerName()).append(":").append(getLocalPort());
        sb.append(getRequestURI());
        return sb.toString();
    }

    public String getRequestURI() {
        return requestLine.getRequestURI();
    }

    private String getRequestBody() {
        return body;
    }

    public String getParameter(String s) {
        String[] parameterValues = getParameterValues(s);
        if (parameterValues.length == 0) {
            return "";
        }
        return getParameterValues(s)[0];
    }

    public Enumeration<String> getParameterNames() {
        Map<String, String[]> map = getParameters();
        return Collections.enumeration(map.keySet());
    }

    public String[] getParameterValues(String name) {
        Map<String, String[]> map = getParameters();
        return map.getOrDefault(name, new String[0]);
    }


    public Map<String, String[]> getParameters() {
        Map<String, String[]> parameterMap = new LinkedHashMap<>();
        if (requestLine.getMethod() == HttpMethod.GET) {
            addRequestBodyParam(parameterMap);
            addQueryStringParam(parameterMap);
            return parameterMap;
        }
        if (requestLine.getMethod() == HttpMethod.POST) {
            addQueryStringParam(parameterMap);
            addRequestBodyParam(parameterMap);
            return parameterMap;
        }
        return parameterMap;
    }

    private void addRequestBodyParam(Map<String, String[]> parameterMap) {
        String requestBody = getRequestBody();
        parseParam(requestBody, parameterMap);
    }

    private void addQueryStringParam(Map<String, String[]> parameterMap) {
        Map<String, String[]> queryParameters = getQueryParameters();
        parameterMap.putAll(queryParameters);
    }

    private void parseParam(String query, Map<String, String[]> parameterMap) {
        if (query.isEmpty()) {
            return;
        }

        String[] params = query.split("&");
        for (String param : params) {
            String[] split = param.split("=");
            String key = split[0];
            String[] values = Arrays.stream(split[1].split(","))
                    .map(value -> URLDecoder.decode(value, StandardCharsets.UTF_8))
                    .toArray(String[]::new);
            parameterMap.put(key, values);
        }
    }

    public boolean hasCookie() {
        String cookie = headers.getOrDefault("Cookie", "");
        return !cookie.isEmpty();
    }

    public Cookie getCookie() {
        String values = headers.getOrDefault("Cookie", "");
        return new Cookie(values);
    }

    public Session getSession(boolean create) {
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        return session;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpVersion getProtocolVersion() {
        return requestLine.getVersion();
    }

    public String getBody() {
        return body;
    }

    public String getScheme() {
        return "http";
    }


    public String getServerName() {
        return "localhost";
    }

    public int getLocalPort() {
        return 8080;
    }
}
