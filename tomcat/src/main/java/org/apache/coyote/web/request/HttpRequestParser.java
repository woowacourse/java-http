package org.apache.coyote.web.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpMethod;

public class HttpRequestParser {

    private static final String START_LINE_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String CONNECT_DELIMITER = "&";
    private static final String ASSIGN_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String BLANK = "";

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        HttpRequestLine httpRequestLine = parseRequestLine(bufferedReader);
        HttpHeaders httpHeaders = parseHeaders(bufferedReader);
        Map<String, String> parameters = parseParameters(httpRequestLine, httpHeaders, bufferedReader);
        return new HttpRequest(httpRequestLine, httpHeaders, parameters);
    }

    private static HttpRequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        return HttpRequestLine.from(bufferedReader.readLine().split(START_LINE_DELIMITER));
    }

    private static HttpHeaders parseHeaders(final BufferedReader bufferedReader) {
        List<Pair> pairs = bufferedReader.lines()
                .takeWhile(line -> !BLANK.equals(line))
                .map(line -> Pair.splitBy(line, HEADER_DELIMITER))
                .collect(Collectors.toList());
        return HttpHeaderFactory.create(pairs.toArray(Pair[]::new));
    }

    private static Map<String, String> parseParameters(final HttpRequestLine httpRequestLine,
                                                       final HttpHeaders httpHeaders,
                                                       final BufferedReader bufferedReader) throws IOException {
        if (httpRequestLine.isSameMethod(HttpMethod.GET) && httpRequestLine.hasQueryParameter()) {
            return parseParameters(httpRequestLine.getQueryParameter());
        }
        if (httpRequestLine.isSameMethod(HttpMethod.POST)) {
            return parseParameters(new String(parseBody(bufferedReader, httpHeaders)));
        }
        return Collections.emptyMap();
    }

    private static Map<String, String> parseParameters(final String content) {
        return Arrays.stream(content.split(CONNECT_DELIMITER))
                .collect(Collectors.toMap(parameter -> parameter.split(ASSIGN_DELIMITER)[KEY_INDEX],
                        parameter -> parameter.split(ASSIGN_DELIMITER)[VALUE_INDEX]));
    }

    private static char[] parseBody(final BufferedReader bufferedReader, final HttpHeaders httpHeaders)
            throws IOException {
        int contentLength = Integer.parseInt(httpHeaders.getContentLength());
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return buffer;
    }
}
