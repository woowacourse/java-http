package org.apache.http.request;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpMethod;

public class HttpRequestParser {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final String HEAD_BODY_SEPARATOR =
            "\r\n\r\n";

    public <T> T parse(
            Class<T> type,
            String input
    ) {
        if (type == HttpTomcatRequest.class) {
            return type.cast(parseHttpRequest(input));
        }

        throw new IllegalArgumentException("지원하지 않는 타입: " + type.getSimpleName());
    }

    private HttpTomcatRequest parseHttpRequest(
            String input
    ) {
        String[] messageParts = input.split(
                HEAD_BODY_SEPARATOR,
                2
        );

        String head = messageParts[0];
        String body = "";

        if (messageParts.length > 1) {
            body = messageParts[1];
        }

        String[] lines = head.split(LINE_SEPARATOR);
        String[] requestLine = parseRequestLine(lines[0]);

        HttpMethod method = HttpMethod.fromString(requestLine[0]);

        URI uri = URI.create(requestLine[1]);

        String path = uri.getPath();
        String protocol = requestLine[2];

        Map<String, String> headers = parseHeaders(lines);

        Map<String, String> queryParameters = parseQueryParameters(uri.getRawQuery());

        return new HttpTomcatRequest(
                method,
                path,
                protocol,
                headers,
                queryParameters,
                body
        );
    }

    private String getBody(String input) {
        String[] messageParts = input.split(
                HEAD_BODY_SEPARATOR,
                2
        );

        return messageParts.length > 1
                ? messageParts[1]
                : "";
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
        Map<String, String> headers =
                new HashMap<>();

        for (int i = 1; i < lines.length; i++) {
            String[] header =
                    lines[i].split(":", 2);

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

    private Map<String, String> parseQueryParameters(
            String query
    ) {
        Map<String, String> parameters =
                new HashMap<>();

        if (query == null || query.isBlank()) {
            return parameters;
        }

        String[] pairs = query.split("&");

        for (String pair : pairs) {
            String[] parameter = pair.split("=", 2);

            String name = decode(parameter[0]);

            String value = parameter.length == 2
                    ? decode(parameter[1])
                    : "";

            parameters.put(name, value);
        }

        return parameters;
    }

    private String decode(String value) {
        return URLDecoder.decode(
                value,
                StandardCharsets.UTF_8
        );
    }
}
