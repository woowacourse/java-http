package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestBuilder {

    private static final String DELIMITER = ":";
    private static final String BLANK = " ";
    private static final String EMPTY = "";
    private static final int START = 0;

    public static HttpRequest makeRequest(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final Map<String, String> headerMap = new HashMap<>();
        String body = EMPTY;
        String reader;
        while ((reader = bufferedReader.readLine()) != null) {
            if (isEmptyString(reader)) {
                if (hasBody(headerMap)) {
                    body = makeRequestBody(bufferedReader);
                }
                break;
            }
            final Integer index = reader.indexOf(DELIMITER);
            headerMap.put(reader.substring(START, index), reader.substring(index + 1).replace(BLANK, EMPTY));
        }
        return new HttpRequest(requestLine, new Header(headerMap), body);
    }

    private static boolean hasBody(final Map<String, String> headerMap) {
        return headerMap.get("Content-Length") != null;
    }

    private static boolean isEmptyString(final String reader) {
        return reader.equals(EMPTY);
    }

    private static String makeRequestBody(final BufferedReader bufferedReader) throws IOException {
        String str;
        if ((str = bufferedReader.readLine()) == null) {
            return EMPTY;
        }
        return bufferedReader.lines().collect(Collectors.joining());
    }
}
