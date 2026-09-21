package org.apache.http.request;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpMethod;
import org.qupring.session.SessionManager;

public class HttpRequestParser {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String HEAD_BODY_SEPARATOR = "\r\n\r\n";

    public <T> T parse(Class<T> type, String input) {
        if (type == HttpTomcatRequest.class) {
            return type.cast(parseHttpRequest(input));
        }

        throw new IllegalArgumentException("지원하지 않는 타입: " + type.getSimpleName());
    }

    private HttpTomcatRequest parseHttpRequest(String input) {
        String[] messageParts =
                input.split(HEAD_BODY_SEPARATOR, 2);

        String head = messageParts[0];
        String body = extractBody(messageParts);

        String[] lines = head.split(LINE_SEPARATOR);
        String[] requestLine = parseRequestLine(lines[0]);

        URI uri = URI.create(requestLine[1]);
        Map<String, String> headers = parseHeaders(lines);
        Map<String, String> parameters = parseParameters(uri.getRawQuery());
        Map<String, String> cookies = parseCookies(headers.get("cookie"));
        Map<String, String> bodyParameters = parseParameters(body);

        SessionManager sessionManager = SessionManager.getInstance();

        return new HttpTomcatRequest(
                HttpMethod.fromString(requestLine[0]),
                uri.getPath(),
                requestLine[2],
                sessionManager.findSession(cookies.get("JSESSIONID")),
                headers,
                parameters,
                cookies,
                bodyParameters
        );
    }

    private String extractBody(String[] messageParts) {
        if (messageParts.length < 2) {
            return null;
        }

        return messageParts[1];
    }

    private String[] parseRequestLine(String line) {
        String[] requestLine = line.split(" ", 3);

        if (requestLine.length != 3) {
            throw new IllegalArgumentException(
                    "올바르지 않은 요청 라인: "
                            + line
            );
        }

        return requestLine;
    }

    private Map<String, String> parseHeaders(
            String[] lines
    ) {
        Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String[] header = lines[i].split(":", 2);

            if (header.length != 2) {
                continue;
            }

            String name = header[0]
                    .trim()
                    .toLowerCase();

            String value = header[1].trim();

            headers.put(name, value);
        }

        return headers;
    }

    private Map<String, String> parseParameters(String input) {
        Map<String, String> parameters = new HashMap<>();

        if (input == null || input.isBlank()) {
            return parameters;
        }

        for (String pair : input.split("&")) {
            String[] parameter = pair.split("=", 2);

            String name = decode(parameter[0]);
            String value = "";

            if (parameter.length == 2) {
                value = decode(parameter[1]);
            }

            parameters.put(name, value);
        }

        return parameters;
    }

    private Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> cookies = new HashMap<>();

        if (cookieHeader == null || cookieHeader.isBlank()) {
            return cookies;
        }

        for (String cookie : cookieHeader.split(";")) {
            String[] pair = cookie.trim().split("=", 2);

            if (pair.length == 2) {
                cookies.put(pair[0].trim(), pair[1].trim());
            }
        }

        return cookies;
    }

    private String decode(String value) {
        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }
}
