package org.apache.coyote.http11.request.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.domain.body.RequestBody;
import org.apache.coyote.http11.domain.header.RequestHeader;
import org.apache.coyote.http11.request.domain.RequestLine;
import org.apache.coyote.http11.request.model.HttpRequest;

public final class HttpRequestConverter {

    public static final String END_OF_HEADER = "";

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
        while (!END_OF_HEADER.equals((headerLine = bufferedReader.readLine()))) {
            headerLines.add(headerLine);
        }
        return new RequestHeader(headerLines);
    }

    private static RequestBody toRequestBody(BufferedReader bufferedReader, int bodyLength) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (bodyLength == 0) {
            return null;
        }
        char[] buffer = new char[bodyLength];
        int readCount = bufferedReader.read(buffer, 0, bodyLength);
        stringBuilder.append(buffer, 0, readCount);

        return new RequestBody(stringBuilder.toString());
    }
}
