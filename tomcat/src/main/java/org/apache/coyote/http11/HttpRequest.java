package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;
    private final HttpCookie cookies;
    private Session session;

    private HttpRequest(final RequestLine requestLine,
                        final Map<String, String> headers,
                        final String body,
                        final Map<String, String> parameters,
                        final HttpCookie cookies,
                        final Session session) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body;
        this.parameters = Map.copyOf(parameters);
        this.cookies = cookies;
        this.session = session;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final BufferedInputStream requestInputStream = new BufferedInputStream(inputStream);

        final String requestLineValue = readLine(requestInputStream);
        if (requestLineValue == null) {
            throw new IllegalArgumentException("HTTP Request Line이 없습니다.");
        }

        final RequestLine requestLine = new RequestLine(requestLineValue);
        final Map<String, String> headers = readHeaders(requestInputStream);
        final String body = readBody(requestInputStream, headers);
        final Map<String, String> parameters = new HashMap<>(
                parseParameters(requestLine.getQueryString())
        );
        parameters.putAll(parseParameters(body));

        final HttpCookie cookies = new HttpCookie(headers.get("cookie"));
        final Session session = findSession(cookies);

        return new HttpRequest(requestLine, headers, body, parameters, cookies, session);
    }

    private static String readLine(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream lineBytes = new ByteArrayOutputStream();

        while (true) {
            final int value = inputStream.read();

            if (value == -1) {
                if (lineBytes.size() == 0) {
                    return null;
                }
                throw new IllegalArgumentException("HTTP 요청의 줄이 완전히 전달되지 않았습니다.");
            }

            if (value == '\r') {
                if (inputStream.read() != '\n') {
                    throw new IllegalArgumentException("HTTP 줄바꿈은 CRLF여야 합니다.");
                }
                return lineBytes.toString(StandardCharsets.UTF_8);
            }

            if (value == '\n') {
                throw new IllegalArgumentException("HTTP 줄바꿈은 CRLF여야 합니다.");
            }

            lineBytes.write(value);
        }
    }

    private static Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        while (true) {
            final String headerLine = readLine(inputStream);

            if (headerLine == null) {
                throw new IllegalArgumentException("HTTP Header가 완전히 전달되지 않았습니다.");
            }
            if (headerLine.isEmpty()) {
                break;
            }

            final String[] headerParts = headerLine.split(":", 2);
            if (headerParts.length != 2) {
                throw new IllegalArgumentException("잘못된 HTTP Header입니다: " + headerLine);
            }

            final String headerName = headerParts[0].trim().toLowerCase(Locale.ROOT);
            final String headerValue = headerParts[1].trim();
            headers.put(headerName, headerValue);
        }
        return headers;
    }

    private static String readBody(final InputStream inputStream,
                                   final Map<String, String> headers) throws IOException {
        final String contentLengthHeader = headers.get("content-length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength;
        try {
            contentLength = Integer.parseInt(contentLengthHeader);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 Content-Length입니다: " + contentLengthHeader, e);
        }

        if (contentLength < 0) {
            throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다.");
        }

        final byte[] bodyBytes = inputStream.readNBytes(contentLength);
        if (bodyBytes.length != contentLength) {
            throw new IllegalArgumentException("Request Body가 완전히 전달되지 않았습니다.");
        }
        return new String(bodyBytes, StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseParameters(final String body) {
        final Map<String, String> parameters = new HashMap<>();
        if (body.isEmpty()) {
            return parameters;
        }

        for (final String parameter : body.split("&")) {
            final String[] parameterParts = parameter.split("=", 2);
            if (parameterParts.length != 2) {
                continue;
            }

            try {
                final String name = URLDecoder.decode(parameterParts[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(parameterParts[1], StandardCharsets.UTF_8);
                parameters.put(name, value);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("잘못된 Form Parameter입니다: " + parameter, e);
            }
        }
        return parameters;
    }

    private static Session findSession(final HttpCookie cookies) {
        final String sessionId = cookies.get("JSESSIONID");
        if (sessionId == null) {
            return null;
        }

        return SessionManager.getInstance().findSession(sessionId);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getVersion() {
        return requestLine.getVersion();
    }

    public String getHeader(final String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getBody() {
        return body;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }

    public Session getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        if (!create) {
            return null;
        }

        final String sessionId = UUID.randomUUID().toString();
        session = new Session(sessionId);
        SessionManager.getInstance().add(session);
        return session;
    }
}
