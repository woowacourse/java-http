package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public record HttpRequestLine(
        String method,
        String uri,
        Map<String, String> queryParameters
) {
    private static final String SP = " ";
    private static final Charset REQUEST_LINE_CHARSET = StandardCharsets.US_ASCII;
    private static final String QUERY_DELIMITER = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private static final int REQUEST_LINE_PARTS_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;

    private static final int KEY_VALUE_PARTS_COUNT = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static HttpRequestLine from(final InputStream inputStream) throws IOException {
        final String requestLine = new BufferedReader(
                new InputStreamReader(inputStream, REQUEST_LINE_CHARSET)).readLine();

        if (requestLine == null) {
            throw new IOException("클라이언트가 요청 없이 연결을 닫았습니다.");
        }

        final String[] requestLineParts = requestLine.split(SP);

        if (requestLineParts.length != REQUEST_LINE_PARTS_COUNT) {
            throw new IOException("잘못된 HTTP Request Line 형식입니다: " + requestLine);
        }

        final String requestTarget = requestLineParts[REQUEST_TARGET_INDEX];
        final int queryIndex = requestTarget.indexOf(QUERY_DELIMITER);
        int notFound = -1;

        if (queryIndex == notFound) {
            return new HttpRequestLine(requestLineParts[METHOD_INDEX], requestTarget, Map.of());
        }

        return new HttpRequestLine(
                requestLineParts[METHOD_INDEX],
                requestTarget.substring(0, queryIndex),
                parseQueryParameters(requestTarget.substring(queryIndex + QUERY_DELIMITER.length()))
        );
    }

    private static Map<String, String> parseQueryParameters(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();

        for (String parameter : queryString.split(PARAMETER_DELIMITER)) {
            if (parameter.isEmpty()) {
                continue;
            }

            final String[] keyValue = parameter.split(KEY_VALUE_DELIMITER, KEY_VALUE_PARTS_COUNT);
            String value = "";
            if (keyValue.length == KEY_VALUE_PARTS_COUNT) {
                value = keyValue[VALUE_INDEX];
            }

            parameters.putIfAbsent(decode(keyValue[KEY_INDEX]), decode(value));
        }

        return Map.copyOf(parameters);
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
