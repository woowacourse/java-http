package org.apache.coyote.http11.web.request;

import org.apache.coyote.http11.exception.InvalidHttpRequestException;
import org.apache.coyote.http11.support.HttpHeader;
import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStartLine;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpRequestParser {

    private static final String START_LINE_DELIMITER = " ";
    private static final int HTTP_HEADER_INDEX = 0;
    private static final int HTTP_HEADER_VALUE_INDEX = 1;

    private HttpRequestParser() {
    }

    public static HttpRequest execute(final BufferedReader bufferedReader) throws IOException {
        final HttpStartLine httpStartLine = parseStartLine(bufferedReader);
        final HttpHeaders httpHeaders = parseHttpHeaders(bufferedReader);
        final char[] body = parseBody(bufferedReader, httpHeaders);

        return new HttpRequest(httpStartLine, httpHeaders, new String(body));
    }

    private static HttpStartLine parseStartLine(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        if (Objects.isNull(startLine) || startLine.isBlank()) {
            throw new InvalidHttpRequestException();
        }

        return HttpStartLine.from(startLine.split(START_LINE_DELIMITER));
    }

    private static HttpHeaders parseHttpHeaders(final BufferedReader bufferedReader) {
        final List<String> strings = bufferedReader.lines()
                .takeWhile(line -> !"".equals(line))
                .collect(Collectors.toList());

        final Map<HttpHeader, String> httpHeaders = strings.stream()
                .map(it -> it.split(": "))
                .filter(it -> HttpHeader.contains(it[HTTP_HEADER_INDEX]))
                .collect(Collectors.toMap(
                        it -> HttpHeader.from(it[HTTP_HEADER_INDEX]),
                        it -> it[HTTP_HEADER_VALUE_INDEX],
                        (x, y) -> y, LinkedHashMap::new));

        return new HttpHeaders(httpHeaders);
    }

    private static char[] parseBody(final BufferedReader bufferedReader, final HttpHeaders httpHeaders)
            throws IOException {
        final int contentLength = Integer.parseInt(httpHeaders.getContentLength());
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return buffer;
    }
}
