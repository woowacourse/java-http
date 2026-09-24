package org.apache.coyote.http11.request;

import org.apache.catalina.session.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;
    private Session session;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final var reader  = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));

        final var firstLine = reader.readLine();

        this.requestLine = RequestLine.parse(firstLine);
        this.headers = readHeaders(reader);
        this.body = readBody(reader);
        this.parameters = parseParameters();
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final var headers = new HashMap<String, String>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] tokens = line.trim().split(":", 2);

            if (tokens.length != 2) {
                throw new IllegalArgumentException("올바르지 않은 HTTP 헤더입니다.");
            }

            final var name = tokens[0].trim().toLowerCase(Locale.ROOT);
            final var value = tokens[1].trim();

            headers.put(name, value);
        }

        return headers;
    }

    private String readBody(final BufferedReader reader) throws IOException {
        final var contentLengthHeader = headers.get("content-length");

        if (contentLengthHeader == null) {
            if ("POST".equals(requestLine.getMethod())) {
                throw new IllegalArgumentException("Content-Length 헤더가 없습니다.");
            }

            return "";
        }

        final var contentLength = Integer.parseInt(contentLengthHeader);

        if (contentLength < 0) {
            throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다.");
        }

        final var buffer = new char[contentLength];

        int offset = 0;

        while (offset < contentLength) {
            final var readLength = reader.read(
                    buffer,
                    offset,
                    contentLength - offset
            );

            if (readLength == -1) {
                throw new IllegalArgumentException(
                        "요청 Body가 Content-Length보다 짧습니다."
                );
            }

            offset += readLength;
        }

        return new String(buffer);
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public String getHeader(final String name) {
        return headers.get(name.trim().toLowerCase(Locale.ROOT));
    }

    public String getBody() {
        return body;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(final Session session) {
        this.session = session;
    }

    public String getPath() {
        final var uri = requestLine.getUri();
        final var queryIndex = uri.indexOf("?");

        if (queryIndex < 0) {
            return uri;
        }

        return uri.substring(0, queryIndex);
    }

    private Map<String, String> parseParameters() {
        final String rawParameters;

        if ("POST".equals(getMethod())) {
            rawParameters = body;
        } else {
            final var queryIndex = getUri().indexOf('?');
            if (queryIndex < 0 || queryIndex == getUri().length() - 1) {
                return Map.of();
            }
            rawParameters = getUri().substring(queryIndex + 1);
        }

        if (rawParameters.isEmpty()) {
            return Map.of();
        }

        return Arrays.stream(rawParameters.split("&"))
                .map(this::parseParameter)
                .collect(Collectors.toMap(
                        pair -> decode(pair[0]),
                        pair -> decode(pair[1])
                ));
    }

    private String[] parseParameter(final String parameter) {
        final var pair = parameter.split("=", 2);

        if (pair.length != 2 || pair[0].isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청 파라미터입니다. " + parameter);
        }

        return pair;
    }

    private String decode(final String parameter) {
        return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
    }

}
