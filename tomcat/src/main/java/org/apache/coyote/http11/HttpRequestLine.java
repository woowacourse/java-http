package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record HttpRequestLine(
        String method,
        String uri,
        Map<String, String> queryString
) {
    private static final String SP = " ";
    private static final Charset REQUEST_LINE_CHARSET = StandardCharsets.US_ASCII;
    private static final String QUERY_DELIMITER = "?";
    private static final String PARAMETER_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    public static HttpRequestLine from(InputStream inputStream) throws IOException {
        final String requestLine = new BufferedReader(
                new InputStreamReader(inputStream, REQUEST_LINE_CHARSET)).readLine();

        if (requestLine == null) {
            throw new IOException("클라이언트가 요청 없이 연결을 닫았습니다.");
        }

        final String[] requestLineParts = requestLine.split(SP);

        if (requestLineParts.length != 3) {
            throw new IOException("잘못된 HTTP Request Line 형식입니다: " + requestLine);
        }

        final String requestTarget = requestLineParts[1];
        final int queryIndex = requestTarget.indexOf(QUERY_DELIMITER);

        if (queryIndex == -1) {
            return new HttpRequestLine(requestLineParts[0], requestTarget, null);
        }

        String[] queryStringParts = requestTarget.substring(queryIndex + 1)
                .split(PARAMETER_DELIMITER);

        Map<String, String> queryString = Arrays.stream(queryStringParts)
                .map(s -> s.split(KEY_VALUE_DELIMITER, 2))
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));

        return new HttpRequestLine(
                requestLineParts[0],
                requestTarget.substring(0, queryIndex),
                queryString
        );
    }
}
