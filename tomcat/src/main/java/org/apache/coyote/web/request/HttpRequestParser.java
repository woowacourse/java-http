package org.apache.coyote.web.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;

public class HttpRequestParser {

    private static final String START_LINE_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        HttpRequestLine httpRequestLine = parseRequestLine(bufferedReader);
        HttpHeaders httpHeaders = parseHeaders(bufferedReader);
        char[] buffer = parseBody(bufferedReader, httpHeaders);
        return new HttpRequest(httpRequestLine, httpHeaders, new String(buffer));
    }

    private static HttpRequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        return HttpRequestLine.from(bufferedReader.readLine().split(START_LINE_DELIMITER));
    }

    private static HttpHeaders parseHeaders(final BufferedReader bufferedReader) {
        List<Pair> pairs = bufferedReader.lines()
                .takeWhile(line -> !"".equals(line))
                .map(line -> Pair.splitBy(line, HEADER_DELIMITER))
                .collect(Collectors.toList());
        return HttpHeaderFactory.create(pairs.toArray(Pair[]::new));
    }

    private static char[] parseBody(final BufferedReader bufferedReader, final HttpHeaders httpHeaders)
            throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.getContentLength());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return buffer;
    }
}
