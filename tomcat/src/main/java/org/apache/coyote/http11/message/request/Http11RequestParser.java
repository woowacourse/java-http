package org.apache.coyote.http11.message.request;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.cookie.RequestCookie;
import org.apache.coyote.http11.io.Http11InputBuffer;
import org.apache.coyote.http11.message.HttpMethod;

public class Http11RequestParser {

    private static final String INVALID_HTTP_VERSION = "HTTP/1.1";

    private final Http11InputBuffer inputBuffer;
    private final Charset defaultHeaderCharset;
    private final Charset defaultBodyCharset;

    public Http11RequestParser(Http11InputBuffer inputBuffer, Charset defaultHeaderCharset,
                               Charset defaultBodyCharset) {
        this.inputBuffer = inputBuffer;
        this.defaultHeaderCharset = defaultHeaderCharset;
        this.defaultBodyCharset = defaultBodyCharset;
    }

    public HttpRequest parseRequest() throws IOException {
        RequestLine requestLine = parseRequestLine();
        HttpRequestHeader httpRequestHeader = parseHeaders();
        String requestBody = parseRequestBody(requestLine, httpRequestHeader);

        return new HttpRequest(requestLine, httpRequestHeader, requestBody);
    }

    private RequestLine parseRequestLine() throws IOException {
        String rawRequestLine = inputBuffer.readLine();
        if (rawRequestLine == null || rawRequestLine.isEmpty()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }
        RequestLine requestLine = RequestLine.createFromRawRequestLine(rawRequestLine);
        checkHttpVersion(requestLine);
        return requestLine;
    }

    private HttpRequestHeader parseHeaders() throws IOException {
        Map<String, String> rawHeaders = new HashMap<>();
        String line;
        while ((line = inputBuffer.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                rawHeaders.put(key, value);
            }
        }
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(rawHeaders);
        parseCookie(httpRequestHeader);
        return httpRequestHeader;
    }

    private void parseCookie(HttpRequestHeader httpRequestHeader) {
        if (httpRequestHeader.contains("Cookie")) {
            RequestCookie requestCookie = parseToCookie(httpRequestHeader.get("Cookie"));
            httpRequestHeader.addCookie(requestCookie);
        }
    }

    private RequestCookie parseToCookie(String rawCookies) {
        Map<String, String> cookieValues = new HashMap<>();
        String[] pairs = rawCookies.split("; ");
        for (String pair : pairs) {
            String[] splitPair = pair.split("=", 2);
            String key = splitPair[0];
            String value = (splitPair.length == 2) ? splitPair[1] : "";
            cookieValues.put(key, value);
        }
        return new RequestCookie(cookieValues);
    }

    private String parseRequestBody(RequestLine requestLine, HttpRequestHeader httpRequestHeader) throws IOException {
        String requestBody = null;
        if (requestLine.httpMethod().equals(HttpMethod.POST) && httpRequestHeader.contains("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeader.get("Content-Length"));
            if (contentLength > 0) {
                byte[] body = inputBuffer.readNBytes(contentLength);
                String contentType = httpRequestHeader.get("Content-Type");
                requestBody = new String(body, extractBodyCharset(contentType));
            }
        }
        return requestBody;
    }

    private Charset extractBodyCharset(String contentType) {
        if (contentType == null) {
            return defaultBodyCharset;
        }
        for (String t : contentType.split(";")) {
            int i = t.indexOf('=');
            if (i > 0 && t.substring(0, i).trim().equalsIgnoreCase("charset")) {
                try {
                    return Charset.forName(t.substring(i + 1).trim());
                } catch (Exception ignore) {
                    return defaultBodyCharset;
                }
            }
        }
        return defaultBodyCharset;
    }

    private void checkHttpVersion(RequestLine requestLine) {
        if (!requestLine.httpVersion().equals(INVALID_HTTP_VERSION)) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 버전입니다.");
        }
    }
}
