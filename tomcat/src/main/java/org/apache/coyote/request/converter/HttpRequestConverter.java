package org.apache.coyote.request.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.request.requestLine.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestConverter {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestConverter.class);

    private HttpRequestConverter() {
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = new RequestLine(bufferedReader.readLine());
        final RequestHeader requestHeader = toRequestHeader(bufferedReader);
        final RequestBody requestBody = toRequestBody(bufferedReader, requestHeader.getBodyLength());

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestHeader toRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();

        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isBlank()) {
            headerLines.add(headerLine);
        }

        return new RequestHeader(headerLines);
    }

    private static RequestBody toRequestBody(final BufferedReader bufferedReader, final int bodyLength) throws IOException {

        char[] buffer = new char[bodyLength];
        int totalRead = 0;
        while (totalRead < bodyLength) {
            int read = bufferedReader.read(buffer, totalRead, bodyLength - totalRead);
            if (read == -1) break;
            totalRead += read;
        }
        return new RequestBody(new String(buffer, 0, totalRead));
    }

}
