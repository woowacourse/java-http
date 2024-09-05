package org.apache.catalina.core;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HttpResponse implements HttpServletResponse {

    private static final String START_LiNE_FORMAT = "%s %s %s ";
    private static final String HEADER_FORMAT_TEMPLATE = "%s: %s \r\n";

    private HttpStatus httpStatus = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String responseBody = "";

    public HttpResponse() {
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return "";
    }

    @Override
    public String encodeRedirectURL(String s) {
        return "";
    }

    @Override
    public String encodeUrl(String s) {
        return "";
    }

    @Override
    public String encodeRedirectUrl(String s) {
        return "";
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {
        headers.put("Location", s);
        setStatus(302);
    }

    @Override
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {
        this.httpStatus = HttpStatus.from(i);
    }

    @Override
    public void setStatus(int i, String s) {
        this.httpStatus = HttpStatus.valueOf(s);
    }

    @Override
    public int getStatus() {
        return httpStatus.getStatusCode();
    }

    @Override
    public String getHeader(String s) {
        return "";
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return List.of();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return List.of();
    }

    @Override
    public String getCharacterEncoding() {
        return "";
    }

    @Override
    public String getContentType() {
        return "";
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {
        headers.put("Content-Length", String.valueOf(i));
    }

    @Override
    public void setContentLengthLong(long l) {

    }

    @Override
    public void setContentType(String s) {
        headers.put("Content-Type", s);
    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }


    public String getResponse() {
        String startLine = START_LiNE_FORMAT.formatted("HTTP/1.1", httpStatus.getStatusCode(), httpStatus.name());
        StringBuilder sb = new StringBuilder(startLine).append("\r\n");
        headers.forEach((name, value) -> sb.append(HEADER_FORMAT_TEMPLATE.formatted(name, value)));
        sb.append("\r\n").append(responseBody);
        return sb.toString();
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
