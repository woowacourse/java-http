package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequestReader {

    private static final String HTTP_HEADER_VALUE_SEPARATOR = ":";

    public static HttpRequest readHttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final String startLine = br.readLine();

        final Map<String, String> headers = readHeaders(br);
        return HttpRequest.of(startLine, headers);
    }

    private static Map<String, String> readHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String readLine = br.readLine();
        while (readLine != null && !readLine.isEmpty()) {
            addHeaderByReadLine(readLine, headers);
            readLine = br.readLine();
        }

        return headers;
    }

    private static void addHeaderByReadLine(final String readLine, final Map<String, String> headers) {
        if (readLine == null || readLine.isEmpty()) {
            return;
        }

        if (readLine.contains(HTTP_HEADER_VALUE_SEPARATOR)) {
            final int index = readLine.indexOf(HTTP_HEADER_VALUE_SEPARATOR);
            final String headerKey = readLine.substring(0, index).strip();
            final String headerValue = readLine.substring(index + 1).strip();
            headers.put(headerKey, headerValue);
            return;
        }

        throw new IllegalStateException("헤더는 key: value 형태여야 합니다.");
    }
}
