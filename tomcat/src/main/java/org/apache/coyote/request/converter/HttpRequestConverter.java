package org.apache.coyote.request.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.body.RequestBody;
import org.apache.coyote.request.requestLine.RequestLine;
import org.apache.coyote.request.header.RequestHeader;

public final class HttpRequestConverter {

    public static final int ZERO = 0;
    public static final int OFF_SET = 0;

    private HttpRequestConverter() {
    }

    public static HttpRequest convertFrom(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = new RequestLine(bufferedReader.readLine());
        RequestHeader requestHeader = toRequestHeader(bufferedReader);
        RequestBody requestBody = toRequestBody(bufferedReader, requestHeader.getBodyLength());

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestHeader toRequestHeader(BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isBlank()) {
            headerLines.add(headerLine);
        }
        return new RequestHeader(headerLines);
    }

    private static RequestBody toRequestBody(BufferedReader bufferedReader, int bodyLength) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (bodyLength == ZERO) {
            return null;
        }
        char[] buffer = new char[bodyLength];
        int readCount = bufferedReader.read(buffer, OFF_SET, bodyLength);
        stringBuilder.append(buffer, OFF_SET, readCount);

        return new RequestBody(stringBuilder.toString());
    }
}
