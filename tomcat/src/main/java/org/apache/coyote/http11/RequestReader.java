package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestReader {

    private final HttpRequest request;

    public RequestReader(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            RequestLine requestLine = readRequestLine(reader);
            RequestHeaders requestHeaders = readRequestHeaders(reader);
            String requestBody = readRequestBody(reader, requestHeaders);
            this.request = new HttpRequest(requestLine, requestHeaders, requestBody);
        }
    }

    private RequestLine readRequestLine(BufferedReader reader) throws IOException {
        return new RequestLine(reader.readLine());
    }

    private RequestHeaders readRequestHeaders(BufferedReader reader) throws IOException {
        List<String> rawRequestHeaders = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                break;
            }
            rawRequestHeaders.add(line);
        }
        return new RequestHeaders(rawRequestHeaders);
    }

    private String readRequestBody(BufferedReader reader, RequestHeaders requestHeaders) throws IOException {
        String contentLengthValue = requestHeaders.getHeaderValue("Content-Length");
        if (contentLengthValue.isEmpty()) {
            return "";
        }
        int contentLength = Integer.parseInt(contentLengthValue);
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public HttpRequest getHttpRequest() {
        return request;
    }
}
