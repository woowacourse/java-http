package org.apache.catalina.core;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.PushBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpRequest implements HttpServletRequest {

    public final String request;

    public HttpRequest(String request) {
        this.request = request;
    }

    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return "";
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String s) {
        return 0;
    }

    @Override
    public HttpServletMapping getHttpServletMapping() {
        return HttpServletRequest.super.getHttpServletMapping();
    }

    @Override
    public String getMethod() {
        String startLine = getStartLine();
        return startLine.split(" ")[0];
    }

    @Override
    public String getPathInfo() {
        return "";
    }

    @Override
    public String getPathTranslated() {
        return "";
    }

    @Override
    public PushBuilder newPushBuilder() {
        return HttpServletRequest.super.newPushBuilder();
    }

    @Override
    public String getContextPath() {
        return "";
    }

    @Override
    public String getQueryString() {
        String path = getStartLine().split(" ")[1];
        int i = path.lastIndexOf("?");
        if (i == -1) {
            return "";
        }
        return path.substring(i + 1);
    }

    @Override
    public String getRemoteUser() {
        return "";
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return "";
    }

    @Override
    public String getRequestURI() { // 리소스만
        String requestTarget = getRequestTarget();
        int queryStringIndex = requestTarget.indexOf('?');
        if (queryStringIndex == -1) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryStringIndex);
    }

    @Override
    public StringBuffer getRequestURL() { // 전체 다
        String[] startLine = getStartLine().split(" ");
        StringBuffer sb = new StringBuffer();
        sb.append(getScheme()).append("://").append(getServerName()).append(":").append(getLocalPort());
        sb.append(startLine[1]);
        return sb;
    }

    private String getRequestTarget() {
        return getStartLine().split(" ")[1];
    }

    private String getStartLine() {
        return request.split("\n")[0];
    }

    private String getRequestBody() {
        String[] requestContent = request.split("\r\n\r\n");
        if (requestContent.length > 1) {
            return requestContent[1];
        }
        return "";
    }

    @Override
    public String getServletPath() {
        return "/";
    }

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public String changeSessionId() {
        return "";
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return List.of();
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Map<String, String> getTrailerFields() {
        return HttpServletRequest.super.getTrailerFields();
    }

    @Override
    public boolean isTrailerFieldsReady() {
        return HttpServletRequest.super.isTrailerFieldsReady();
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String s) {
        String[] parameterValues = getParameterValues(s);
        if (parameterValues.length == 0) {
            return null;
        }
        return getParameterValues(s)[0];
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Map<String, String[]> map = getParameterMap();
        return Collections.enumeration(map.keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        Map<String, String[]> map = getParameterMap();
        if (map.containsKey(s)) {
            return map.get(s);
        }
        return new String[0];
    }

    @Override
    public Map<String, String[]> getParameterMap() { // TODO getRequestURL 잘못 쓰임
        Map<String, String[]> map = new LinkedHashMap<>();
        if (getMethod().equals("GET")) {
            addRequestBodyParam(map);
            addQueryStringParam(map);
            return map;
        }
        if (getMethod().equals("POST")) {
            addQueryStringParam(map);
            addRequestBodyParam(map);
            return map;
        }
        return map;
    }

    private void addRequestBodyParam(Map<String, String[]> map) {
        String requestBody = getRequestBody();
        parseParam(requestBody, map);
    }

    private void addQueryStringParam(Map<String, String[]> map) {
        String requestUrl = getRequestURL().toString();
        int i = requestUrl.indexOf('?');
        if (i == -1) {
            return;
        }
        String query = requestUrl.substring(i + 1);
        parseParam(query, map);
    }

    private void parseParam(String query, Map<String, String[]> map) {
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
            map.put(key, values);
        }
    }

    @Override
    public String getProtocol() {
        return "";
    }

    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return "localhost";
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return "";
    }

    @Override
    public String getRemoteHost() {
        return "";
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return "";
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "";
    }

    @Override
    public String getLocalAddr() {
        return "";
    }

    @Override
    public int getLocalPort() {
        return 8080;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }
}
