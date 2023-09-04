package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.vo.HttpBody;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.Url;

public class HttpRequestParser {

    public static HttpRequest parse(final BufferedReader bufferedReader) {
        final String startLine = readLine(bufferedReader);
        final HttpHeaders httpHeaders = parseHeader(bufferedReader);
        final Integer contentLength = parseContentLength(httpHeaders);
        final HttpBody body = parseBody(bufferedReader, contentLength);

        return new HttpRequest.Builder()
                .httpMethod(HttpMethod.valueOf(startLine.split(" ")[0]))
                .url(Url.from(startLine.split(" ")[1]))
                .headers(httpHeaders)
                .body(body)
                .build();
    }

    private static Integer parseContentLength(final HttpHeaders httpHeaders) {
        final String rawContentLength = httpHeaders.getRecentHeaderValue(HttpHeader.CONTENT_LENGTH);

        if (rawContentLength == null) {
            return null;
        }
        return Integer.parseInt(rawContentLength);
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

    private static HttpBody parseBody(final BufferedReader bufferedReader, final Integer contentLength) {
        if (contentLength == null) {
            return HttpBody.getEmptyBody();
        }
        char[] chars = new char[contentLength];
        try {
            bufferedReader.read(chars);
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }

        return parseRawBody(new String(chars));
    }

    public static HttpBody parseRawBody(final String rawBody) {
        final HttpBody body = HttpBody.getEmptyBody();
        final String[] keyValuePairs = rawBody.split("&");

        Arrays.stream(keyValuePairs).forEach(it -> {
            String[] keyValuePair = it.split("=");
            String decodedValue = decodeValue(keyValuePair[1], "UTF-8");
            body.put(keyValuePair[0], decodedValue);
        });

        return body;
    }

    public static String decodeValue(final String value, final String encoding) {
        try {
            return URLDecoder.decode(value, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedServletException(e);
        }
    }

    public static String readLine(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new UncheckedServletException(e);
        }
    }
}

