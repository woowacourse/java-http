package org.apache.coyote.http11.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.RequestCookie;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpRequest;
import org.apache.coyote.http11.message.HttpRequestHeader;
import org.apache.coyote.http11.message.RequestLine;

public class Http11InputBuffer {

    private static final int END_SIGN_FOR_STREAM = -1;
    private static final String INVALID_HTTP_VERSION = "HTTP/1.1";

    private final InputStream inputStream;
    private final Charset defaultHeaderCharset;
    private final Charset defaultBodyCharset;

    public Http11InputBuffer(InputStream inputStream, Charset defaultHeaderCharset, Charset defaultBodyCharset) {
        this.inputStream = inputStream;
        this.defaultHeaderCharset = defaultHeaderCharset;
        this.defaultBodyCharset = defaultBodyCharset;
    }

    public HttpRequest read() throws IOException {
        String rawRequestLine = readLine(inputStream);
        if (rawRequestLine == null || rawRequestLine.isEmpty()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }

        RequestLine requestLine = RequestLine.createFromRawRequestLine(rawRequestLine);
        checkHttpVersion(requestLine);

        Map<String, String> rawHeaders = parseHeaders(inputStream);
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(rawHeaders);

        RequestCookie requestCookie = parseCookie(httpRequestHeader);
        httpRequestHeader.addCookie(requestCookie);

        String requestBody = parseRequestBody(requestLine, httpRequestHeader);

        return new HttpRequest(requestLine, httpRequestHeader, requestBody);
    }

    private RequestCookie parseCookie(HttpRequestHeader httpRequestHeader) {
        RequestCookie requestCookie = null;
        if (httpRequestHeader.contains("Cookie")) {
            String rawCookie = httpRequestHeader.get("Cookie");
            requestCookie = parseToCookie(rawCookie);
            httpRequestHeader.addCookie(requestCookie);
        }
        return requestCookie;
    }

    private static void checkHttpVersion(RequestLine requestLine) {
        if (!requestLine.httpVersion().equals(INVALID_HTTP_VERSION)) {
            throw new IllegalArgumentException("지원하지 않는 HTTP 버전입니다.");
        }
    }

    private String readRequestBody(int contentLength, HttpRequestHeader httpRequestHeader, String requestBody)
            throws IOException {
        byte[] body = inputStream.readNBytes(contentLength);

        if (httpRequestHeader.contains("Content-Type")) {
            String contentType = httpRequestHeader.get("Content-Type");
            requestBody = new String(body, extractBodyCharset(contentType));
        }
        return requestBody;
    }

    private String parseRequestBody(RequestLine requestLine, HttpRequestHeader httpRequestHeader) throws IOException {
        String requestBody = null;

        if (requestLine.httpMethod().equals(HttpMethod.POST) && httpRequestHeader.contains("Content-Length")) {
            int contentLength = Integer.parseInt(httpRequestHeader.get("Content-Length"));
            if (contentLength > 0) {
                requestBody = readRequestBody(contentLength, httpRequestHeader, requestBody);
            }
        }
        return requestBody;
    }

    private String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int readByte;
        boolean seenCR = false;

        while ((readByte = inputStream.read()) != END_SIGN_FOR_STREAM) {
            if (readByte == '\r') {
                seenCR = true;
                continue;
            }
            if (seenCR && readByte == '\n') {
                break;
            }
            if (seenCR) {
                buffer.write('\r');
                seenCR = false;
            }
            buffer.write(readByte);
        }

        if (readByte == END_SIGN_FOR_STREAM && buffer.size() == 0) {
            return null;
        }
        return buffer.toString(defaultHeaderCharset);
    }

    private Map<String, String> parseHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }
        return headers;
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
}
