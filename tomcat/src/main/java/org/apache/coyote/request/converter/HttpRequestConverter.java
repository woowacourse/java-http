package org.apache.coyote.request.converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.RequestBody;
import org.apache.coyote.request.RequestHeader;
import org.apache.coyote.request.requestInfo.RequestInfo;

public class HttpRequestConverter {

    private HttpRequestConverter() {
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final RequestInfo requestInfo = new RequestInfo(bufferedReader.readLine());
        final RequestHeader requestHeader = toRequestHeader(bufferedReader);
        final RequestBody requestBody = toRequestBody(bufferedReader);

        return new HttpRequest(requestInfo, requestHeader, requestBody);
    }

    private static RequestHeader toRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> headerLines = new ArrayList<>();

        String headerLine;
        while(bufferedReader.ready()){
            headerLine = bufferedReader.readLine();
            if(headerLine.isBlank())break;
            headerLines.add(headerLine);
        }

        return new RequestHeader(headerLines);
    }

    private static RequestBody toRequestBody(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        while(bufferedReader.ready()){
            stringBuilder.append(bufferedReader.readLine());
        }

        return new RequestBody(stringBuilder.toString());
    }
}
