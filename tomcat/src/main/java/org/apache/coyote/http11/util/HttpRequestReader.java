package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.exception.InvalidHttpFormException;

public class HttpRequestReader {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String REQUEST_HEADER_DELIMITER = ": ";

    private HttpRequestReader() {
    }

    public static HttpRequest read(final BufferedReader bufferedReader) throws IOException {
        final var httpRequest = new HttpRequest();
        readRequestLine(httpRequest, bufferedReader);
        readRequestHeaders(httpRequest, bufferedReader);
        readRequestBody(httpRequest, bufferedReader);

        return httpRequest;
    }

    private static void readRequestLine(HttpRequest httpRequest, BufferedReader bufferedReader)
            throws IOException {
        final var requestLine = bufferedReader.readLine();
        if (requestLine == null) {
            throw new InvalidHttpFormException();
        }
        final var splitLine = requestLine.split(REQUEST_LINE_DELIMITER);
        httpRequest.setHttpMethod(HttpMethod.valueOf(splitLine[0]));
        httpRequest.setUri(splitLine[1]);
        httpRequest.setPath(HttpParser.parsePath(splitLine[1]));
        httpRequest.addParameters(HttpParser.parseQueryParameters(splitLine[1]));
        httpRequest.setProtocol(splitLine[2]);
    }

    private static void readRequestHeaders(HttpRequest httpRequest, BufferedReader bufferedReader)
            throws IOException {
        final Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            final var header = bufferedReader.readLine();
            if (header.isEmpty()) {
                break;
            }
            final var keyValuePair = header.split(REQUEST_HEADER_DELIMITER);
            headers.put(keyValuePair[0], keyValuePair[1]);
        }
        httpRequest.addHeaders(headers);
    }

    private static void readRequestBody(HttpRequest httpRequest, BufferedReader bufferedReader)
            throws IOException {
        if (!httpRequest.containsHeader(HttpHeaders.CONTENT_LENGTH)) {
            return;
        }
        int contentLength = Integer.parseInt(httpRequest.getHeader(HttpHeaders.CONTENT_LENGTH));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        httpRequest.setBody(requestBody);
    }
}
