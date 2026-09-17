package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public record HttpRequestLine(
        String method,
        String uri
) {
    private static final String SP = " ";
    private static final Charset REQUEST_LINE_CHARSET = StandardCharsets.US_ASCII;


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

        return new HttpRequestLine(requestLineParts[0], requestLineParts[1]);
    }
}
