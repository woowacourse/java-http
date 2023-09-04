package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.exception.ServerException;

public class HttpRequestParser {

    private static final String DELIMITER = " ";
    private static final String EMPTY_LINE = "";
    private static final String HEADER_DELIMITER = ": ";
    private static final int EXIST_HEADER_VALUE = 2;

    public HttpRequest parse(final BufferedReader reader) {
        final String firstLine = readLine(reader);
        final HttpMethod httpMethod = parseHttpMethod(firstLine);
        final String requestUri = parseRequestUri(firstLine);
        final HttpHeaders httpHeaders = parseHttpHeaders(reader);

        return new HttpRequest(httpMethod, requestUri, httpHeaders);
    }

    private String readLine(final BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (final IOException e) {
            throw new ServerException("요청을 읽어오는데 실패했습니다.");
        }
    }

    private HttpMethod parseHttpMethod(final String line) {
        return HttpMethod.valueOf(line.split(DELIMITER)[0]);
    }

    private String parseRequestUri(final String line) {
        return line.split(DELIMITER)[1];
    }

    private HttpHeaders parseHttpHeaders(final BufferedReader reader) {
        String line = readLine(reader);
        final HttpHeaders httpHeaders = new HttpHeaders();
        while (!EMPTY_LINE.equals(line)) {
            final String[] header = line.split(HEADER_DELIMITER);
            if (header.length == EXIST_HEADER_VALUE) {
                final String key = header[0];
                final String value = header[1];
                httpHeaders.add(key, value);
            }
            line = readLine(reader);
        }
        return httpHeaders;
    }
}
