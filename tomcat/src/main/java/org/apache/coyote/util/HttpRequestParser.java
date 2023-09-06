package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.vo.HttpBody;
import org.apache.coyote.http.vo.HttpHeaders;
import org.apache.coyote.http.vo.HttpRequest;
import org.apache.coyote.http.vo.HttpRequest.Builder;
import org.apache.coyote.http.vo.Url;

public class HttpRequestParser {

    public static HttpRequest parse(final BufferedReader bufferedReader) {
        final String startLine = readLine(bufferedReader);
        final HttpHeaders httpHeaders = parseHeader(bufferedReader);
        final HttpRequest.Builder requestBuilder = new Builder()
                .httpMethod(HttpMethod.valueOf(startLine.split(" ")[0]))
                .url(Url.from(startLine.split(" ")[1]))
                .headers(httpHeaders);

        final Optional<Integer> contentLength = parseContentLength(httpHeaders);
        contentLength.ifPresent(length -> requestBuilder.body(parseBody(bufferedReader, length)));

        return requestBuilder.build();
    }

    private static Optional<Integer> parseContentLength(final HttpHeaders httpHeaders) {
        final Optional<String> rawContentLength = httpHeaders.getRecentHeaderValue(HttpHeader.CONTENT_LENGTH);
        return rawContentLength.map(Integer::parseInt);
    }

    private static HttpHeaders parseHeader(final BufferedReader bufferedReader) {
        final HttpHeaders httpHeaders = HttpHeaders.getEmptyHeaders();

        String rawHeader;
        while ((rawHeader = readLine(bufferedReader)) != null && !rawHeader.isBlank()) {
            final String[] keyWithValue = rawHeader.split(":");
            final Optional<HttpHeader> key = HttpHeader.from(keyWithValue[0]);

            key.ifPresent(header -> httpHeaders.putAll(header,
                    Arrays.stream(keyWithValue[1].split(","))
                            .map(String::trim)
                            .collect(Collectors.toUnmodifiableList()))
            );
        }
        return httpHeaders;
    }

    private static HttpBody parseBody(final BufferedReader bufferedReader, final Integer contentLength) {
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

