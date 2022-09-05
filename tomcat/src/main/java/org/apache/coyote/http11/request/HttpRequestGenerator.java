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

        final List<String> massages = readMassage(bufferedReader);
        final List<String> headers = extractHeaders(massages);
        final List<String> bodies = extractBodies(massages, headers);

        return HttpRequest.of(requestLine, headers, bodies);
    }

    private static List<String> readMassage(final BufferedReader bufferedReader) throws IOException {
        final List<String> messages = new ArrayList<>();
        while (bufferedReader.ready()) {
            messages.add(bufferedReader.readLine());
        }
        return messages;
    }

    private static List<String> extractHeaders(final List<String> massages) {
        return massages.stream()
            .takeWhile(message -> !message.equals(HttpMessageDelimiter.HEADER_BODY.getValue()))
            .collect(Collectors.toList());
    }

    private static List<String> extractBodies(final List<String> massages, final List<String> headers) {
        return massages.stream()
            .skip(headers.size() + 1)
            .collect(Collectors.toList());
    }
}
