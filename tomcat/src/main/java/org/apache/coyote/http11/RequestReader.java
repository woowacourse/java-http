package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RequestReader {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final String requestBody;

    public RequestReader(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        this.requestLine = readRequestLine(reader);
        this.requestHeaders = readRequestHeaders(reader);
        this.requestBody = readRequestBody(reader, this.requestHeaders);
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

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
