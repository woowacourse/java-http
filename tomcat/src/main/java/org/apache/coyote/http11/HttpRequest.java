package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        final String requestLineValue = reader.readLine();
        if (requestLineValue == null) {
            throw new IllegalArgumentException("HTTP Request Line이 없습니다.");
        }

        final RequestLine requestLine = new RequestLine(requestLineValue);
        final Map<String, String> headers = readHeaders(reader);
        final String body = readBody(reader, headers);
        final Map<String, String> parameters = new HashMap<>(
                parseParameters(requestLine.getQueryString())
        );
        parameters.putAll(parseParameters(body));

        final HttpCookie cookies = new HttpCookie(headers.get("cookie"));
        final Session session = findSession(cookies);

        return new HttpRequest(requestLine, headers, body, parameters, cookies, session);
    }

    private static Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();

        while (true) {
            final String headerLine = reader.readLine();

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

    private static String readBody(final BufferedReader reader,
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

        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int readCount = reader.read(buffer, totalRead, contentLength - totalRead);
            if (readCount == -1) {
                throw new IllegalArgumentException("Request Body가 완전히 전달되지 않았습니다.");
            }
            totalRead += readCount;
        }
        return new String(buffer);
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
