package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.coyote.http11.common.HttpMessageDelimiter;

public class HttpRequestGenerator {

    private HttpRequestGenerator() {
    }

    public static HttpRequest createHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();

        final List<String> messages = readMessage(bufferedReader);
        final List<String> headers = extractHeaders(messages);
        final List<String> bodies = extractBodies(messages, headers);

        return HttpRequest.of(requestLine, headers, bodies);
    }

    private static List<String> readMessage(final BufferedReader bufferedReader) throws IOException {
        final List<String> messages = new ArrayList<>();
        while (bufferedReader.ready()) {
            messages.add(bufferedReader.readLine());
        }
        return messages;
    }

    private static List<String> extractHeaders(final List<String> messages) {
        return messages.stream()
            .takeWhile(HttpMessageDelimiter.HEADER_BODY::isDifference)
            .collect(Collectors.toList());
    }

    private static List<String> extractBodies(final List<String> messages, final List<String> headers) {
        return messages.stream()
            .skip(headers.size() + 1)
            .collect(Collectors.toList());
    }
}
