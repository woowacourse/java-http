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
import java.util.Map;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String requestedSessionId;

    private Session session;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        requestLine = new RequestLine(reader.readLine());
        headers = readHeaders(reader);

        final var requestBody = readBody(reader);
        parameters = findParameters(requestBody);
        requestedSessionId = HttpCookie.parse(headers.get("Cookie")).get("JSESSIONID");
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getRequestedSessionId() {
        return requestedSessionId;
    }

    public Session getSession(final boolean create) {
        if (session != null) {
            return session;
        }

        if (requestedSessionId != null) {
            final var foundSession = SessionManager.getInstance().findSession(requestedSessionId);
            if (foundSession instanceof Session) {
                session = (Session) foundSession;
                return session;
            }
        }

        if (create) {
            session = SessionManager.getInstance().createSession();
        }

        return session;
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> requestHeaders = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final var separatorIndex = line.indexOf(':');
            if (separatorIndex > 0) {
                requestHeaders.put(
                        line.substring(0, separatorIndex).trim(),
                        line.substring(separatorIndex + 1).trim()
                );
            }
        }

        return requestHeaders;
    }

    private String readBody(final BufferedReader reader) throws IOException {
        final var contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }

        final var buffer = new char[Integer.parseInt(contentLength)];
        var offset = 0;

        while (offset < buffer.length) {
            final var readLength = reader.read(buffer, offset, buffer.length - offset);
            if (readLength < 0) {
                break;
            }
            offset += readLength;
        }

        return new String(buffer, 0, offset);
    }

    private Map<String, String> findParameters(final String requestBody) {
        if ("POST".equals(getMethod())) {
            return parseParameters(requestBody);
        }

        final var uri = requestLine.getUri();
        final var queryIndex = uri.indexOf('?');
        if (queryIndex < 0 || queryIndex == uri.length() - 1) {
            return Map.of();
        }

        return parseParameters(uri.substring(queryIndex + 1));
    }

    private Map<String, String> parseParameters(final String value) {
        if (value.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parsedParameters = new HashMap<>();

        for (final var parameter : value.split("&")) {
            final var nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length == 2) {
                parsedParameters.put(
                        URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(nameAndValue[1], StandardCharsets.UTF_8)
                );
            }
        }

        return parsedParameters;
    }
}
