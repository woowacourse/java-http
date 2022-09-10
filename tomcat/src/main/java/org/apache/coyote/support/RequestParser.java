package org.apache.coyote.support;

import org.apache.catalina.exception.FileAccessException;
import org.apache.catalina.exception.NotFoundException;
import org.apache.coyote.Headers;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public class RequestParser {

    private static final String BLANK_DELIMITER = " ";
    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final String URI_DELIMITER = "\\?";
    private static final int URL_INDEX = 1;
    private static final int URI_INDEX = 0;
    private static final int QUERY_PARAMETER_INDEX = 1;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private RequestParser() {
    }

    public static Request parse(final BufferedReader bufferedReader) {
        final RequestInfo requestInfo = extractRequestInfo(bufferedReader);
        final Headers headers = extractHeaders(bufferedReader);
        final String body = extractBody(bufferedReader, Integer.parseInt(headers.find(HttpHeader.CONTENT_LENGTH)));
        return new Request(requestInfo, headers, body);
    }

    private static RequestInfo extractRequestInfo(final BufferedReader bufferedReader) {
        final String line = readLine(bufferedReader);
        final Optional<HttpMethod> httpMethod = HttpMethod.find(line);
        if (httpMethod.isPresent()) {
            return new RequestInfo(httpMethod.get(), getUri(line), getQueryString(line));
        }
        throw new NotFoundException("요청 정보를 찾을 수 없음");
    }

    private static String getUri(final String line) {
        final String url = line.split(BLANK_DELIMITER)[URL_INDEX];
        return url.split(URI_DELIMITER)[URI_INDEX];
    }

    private static String getQueryString(final String line) {
        final String url = line.split(BLANK_DELIMITER)[URL_INDEX];
        final String[] split = url.split(URI_DELIMITER);
        if (split.length <= 1) {
            return "";
        }
        return split[QUERY_PARAMETER_INDEX];
    }

    private static Headers extractHeaders(final BufferedReader bufferedReader) {
        final Headers headers = new Headers();
        String line;
        while (isNotEnd(line = readLine(bufferedReader))) {
            final String[] headerKeyValue = line.split(KEY_VALUE_DELIMITER);
            final Optional<HttpHeader> httpHeader = HttpHeader.find(headerKeyValue[KEY_INDEX]);
            httpHeader.ifPresent(header -> headers.put(header, headerKeyValue[VALUE_INDEX].trim()));
        }
        return headers;
    }

    private static String readLine(final BufferedReader bufferedReader) {
        try {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }

    private static boolean isNotEnd(final String line) {
        return line != null && !line.isBlank();
    }

    private static String extractBody(final BufferedReader bufferedReader, final int contentLength) {
        final char[] body = new char[contentLength];
        readWithOffset(bufferedReader, body, contentLength);
        return new String(body);
    }

    private static void readWithOffset(final BufferedReader bufferedReader, final char[] body, final int contentLength) {
        try {
            bufferedReader.read(body, 0, contentLength);
        } catch (IOException e) {
            throw new FileAccessException();
        }
    }
}
