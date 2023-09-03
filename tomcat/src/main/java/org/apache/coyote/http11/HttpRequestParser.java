package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.Url;

public class HttpRequestParser {

    public static HttpRequest parse(final BufferedReader bufferedReader) {
        final String startLine = readLine(bufferedReader);
        final HttpHeaders httpHeaders = parseHeader(bufferedReader);

        return new HttpRequest.Builder()
                .httpMethod(HttpMethod.valueOf(startLine.split(" ")[0]))
                .url(Url.from(startLine.split(" ")[1]))
                .headers(httpHeaders)
                .build();
    }

    private static HttpHeaders parseHeader(final BufferedReader bufferedReader) {
        final HttpHeaders httpHeaders = HttpHeaders.getEmptyHeaders();

        String rawHeader;
        while ((rawHeader = readLine(bufferedReader)) != null && !rawHeader.isBlank()) {
            final String[] keyWithValue = rawHeader.split(":");
            final HttpHeader key = HttpHeader.from(keyWithValue[0]);

            if (key != null) {
                httpHeaders.putAll(key,
                        Arrays.stream(keyWithValue[1].split(","))
                                .map(String::trim)
                                .collect(Collectors.toUnmodifiableList())
                );
            }
        }
        return httpHeaders;
    }

    public static String readLine(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

