package org.apache.coyote.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpRequestLine;

public class HttpRequestUtils {

    private static final String MESSAGE_DELIMITER = " ";

    private static final int METHOD = 0;
    private static final int URI = 1;
    private static final int VERSION = 2;

    public static HttpRequest newHttpRequest(final BufferedReader bufferedReader) throws IOException {
        HttpRequestLine httpRequestStartLine = toRequestLine(bufferedReader);
        HttpHeaders httpHeaders = toHeaders(bufferedReader);
        if (httpRequestStartLine.isEqualToMethod(HttpMethod.POST)) {
            String body = toBody(httpHeaders, bufferedReader);
            return new HttpRequest(httpRequestStartLine, httpHeaders, body);
        }
        return new HttpRequest(httpRequestStartLine, httpHeaders, "");
    }

    private static HttpRequestLine toRequestLine(final BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        String[] split = startLine.split(" ");
        return new HttpRequestLine(split[METHOD], split[URI], split[VERSION]);
    }

    private static HttpHeaders toHeaders(final BufferedReader bufferedReader) {
        return HttpHeaders.of(bufferedReader.lines()
                .takeWhile(line -> !"".equals(line))
                .map(line -> line.split(MESSAGE_DELIMITER))
                .collect(Collectors.toList()));
    }

    private static String toBody(final HttpHeaders httpHeaders, final BufferedReader reader) throws IOException {
        String value = httpHeaders.getValue("Content-Length");
        int contentLength = Integer.parseInt(value);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
