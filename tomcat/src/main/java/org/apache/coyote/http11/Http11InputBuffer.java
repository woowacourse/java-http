package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.RequestCookie;
import org.apache.catalina.SessionManager;

public class Http11InputBuffer {

    private static final int END_SIGN_FOR_STREAM = -1;

    private final InputStream inputStream;
    private final SessionManager sessionManager;
    private final Charset defaultHeaderCharset;
    private final Charset defaultBodyCharset;

    public Http11InputBuffer(InputStream inputStream, SessionManager sessionManager, Charset defaultHeaderCharset,
                             Charset defaultBodyCharset) {
        this.inputStream = inputStream;
        this.sessionManager = sessionManager;
        this.defaultHeaderCharset = defaultHeaderCharset;
        this.defaultBodyCharset = defaultBodyCharset;
    }

    public HttpRequest read() throws IOException {
        String requestLine = readLine(inputStream);
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("요청 형식이 잘못되었습니다.");
        }

        String[] splitRequestLine = requestLine.split(" ");
        if (splitRequestLine.length < 3) {
            throw new IllegalArgumentException("요청 라인 형식 오류: " + requestLine);
        }
        String httpMethod = splitRequestLine[0];
        String uri = splitRequestLine[1];
        String httpVersion = splitRequestLine[2];

        Map<String, String> headers = parseHeaders(inputStream);

        String host = headers.getOrDefault("host", "");
        String contentType = headers.getOrDefault("content-type", "");
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        String rawCookie = headers.getOrDefault("cookie", "");

        String requestBody = null;
        if ("POST".equalsIgnoreCase(httpMethod) && contentLength > 0) {
            byte[] body = inputStream.readNBytes(contentLength);
            requestBody = new String(body, extractBodyCharset(contentType));
        }

        RequestCookie requestCookie = null;
        if (!rawCookie.isEmpty()) {
            requestCookie = parseToCookie(rawCookie);
        }

        return new HttpRequest(
                sessionManager,
                httpMethod,
                uri,
                httpVersion,
                host,
                contentType,
                requestBody,
                requestCookie
        );
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
                String key = line.substring(0, colonIndex).toLowerCase().trim();
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
