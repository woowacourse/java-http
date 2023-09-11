package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.header.Headers;
import org.apache.coyote.http11.request.header.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public class RequestMapper {

    private static final String END_OF_HEADERS = "";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String EMPTY = "";
    private static final int ZERO = 0;

    private RequestMapper() {
    }

    public static HttpRequest toRequest(final InputStream inputStream) {
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final List<String> headerLines = readHeaderLines(bufferedReader);

            final RequestLine requestLine = RequestLine.from(headerLines);
            final Headers headers = Headers.of(headerLines);
            final Parameters parameters = parameters(headerLines, bufferedReader);

            return new HttpRequest(requestLine, headers, parameters);
        } catch (Exception e) {
            throw new IllegalArgumentException("잘못된 요청입니다.", e);
        }
    }

    private static List<String> readHeaderLines(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();

        while (!headerLines.contains(END_OF_HEADERS)) {
            headerLines.add(bufferedReader.readLine());
        }

        return headerLines;
    }

    private static String find(final List<String> headerLines, final String key) {
        return headerLines.stream()
                .filter(line -> line.contains(key))
                .findAny()
                .orElse(null);
    }

    private static Parameters parameters(final List<String> headerLines,
                                         final BufferedReader bufferedReader) throws IOException {
        final int contentLength = contentLength(headerLines);

        if (contentLength == ZERO) {
            return Parameters.empty();
        }

        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return Parameters.from(buffer);
    }

    private static int contentLength(final List<String> headerLines) {
        final String contentLengthLine = find(headerLines, CONTENT_LENGTH);

        if (isNull(contentLengthLine)) {
            return ZERO;
        }
        return parseToInteger(contentLengthLine);
    }

    private static int parseToInteger(final String contentLengthLine) {
        final String contentLengthValue = contentLengthLine.replace(CONTENT_LENGTH, EMPTY);
        return Integer.parseInt(contentLengthValue);
    }
}
