package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie cookies;
    private final Map<String, String> parameters = new HashMap<>();
    private SessionContext sessionContext;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.cookies = new HttpCookie(headers.get("Cookie"));
        addParameters(requestLine.getQuery());
        String contentType = headers.get("Content-Type");
        if (contentType == null || contentType.split(";", 2)[0].trim()
                .equalsIgnoreCase("application/x-www-form-urlencoded")) {
            addParameters(body);
        }
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        String firstLine = readLine(inputStream);
        if (firstLine == null) {
            return null;
        }

        RequestLine requestLine = new RequestLine(firstLine);
        Map<String, String> headers = readHeaders(inputStream);
        if (headers.containsKey("Transfer-Encoding")) {
            throw new IllegalArgumentException("Transfer-Encoding은 지원하지 않습니다.");
        }

        int contentLength = parseContentLength(headers.get("Content-Length"));
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IllegalArgumentException("요청 본문이 Content-Length보다 짧습니다.");
        }
        return new HttpRequest(requestLine, headers, new String(body, StandardCharsets.UTF_8));
    }

    private static Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        while (true) {
            String line = readLine(inputStream);
            if (line == null) {
                throw new IllegalArgumentException("요청 헤더가 끝나기 전에 연결이 종료되었습니다.");
            }
            if (line.isEmpty()) {
                return headers;
            }

            String[] header = line.split(":", 2);
            if (header.length != 2 || header[0].isBlank()) {
                throw new IllegalArgumentException("올바르지 않은 요청 헤더입니다.");
            }
            String name = header[0].trim();
            if (name.equalsIgnoreCase("Content-Length") && headers.containsKey(name)) {
                throw new IllegalArgumentException("Content-Length가 중복되었습니다.");
            }
            headers.put(name, header[1].trim());
        }
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1) {
            if (value == '\r') {
                if (inputStream.read() != '\n') {
                    throw new IllegalArgumentException("요청의 줄바꿈은 CRLF여야 합니다.");
                }
                return buffer.toString(StandardCharsets.ISO_8859_1);
            }
            if (value == '\n') {
                throw new IllegalArgumentException("요청의 줄바꿈은 CRLF여야 합니다.");
            }
            buffer.write(value);
        }
        if (buffer.size() != 0) {
            throw new IllegalArgumentException("요청 줄이 완성되기 전에 연결이 종료되었습니다.");
        }
        return null;
    }

    private static int parseContentLength(String value) {
        if (value == null) {
            return 0;
        }
        if (!value.matches("[0-9]+")) {
            throw new IllegalArgumentException("Content-Length는 0 이상의 정수여야 합니다.");
        }
        return Integer.parseInt(value);
    }

    private void addParameters(String encodedParameters) {
        if (encodedParameters == null || encodedParameters.isEmpty()) {
            return;
        }
        for (String parameter : encodedParameters.split("&")) {
            if (parameter.isEmpty()) {
                continue;
            }
            String[] pair = parameter.split("=", 2);
            String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "";
            parameters.put(name, value);
        }
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public void setSessionContext(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    public Session getSession() {
        return sessionContext.getSession();
    }

    public Session renewSession() {
        return sessionContext.renewSession();
    }
}
