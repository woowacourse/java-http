package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestReader {

    private final BufferedReader requestReader;

    public HttpRequestReader(BufferedReader requestReader) {
        this.requestReader = requestReader;
    }

    public HttpRequest read() throws IOException {
        List<String> requestHead = readRequestHead(requestReader);
        HttpRequest.Builder requestBuilder = new HttpRequest.Builder().requestHead(requestHead);

        int contentLength = requestBuilder.getBodyLength();
        requestBuilder.body(readBody(contentLength));

        return requestBuilder.build();
    }

    private List<String> readRequestHead(BufferedReader requestReader) throws IOException {
        List<String> requestHead = new ArrayList<>();
        String line;
        while ((line = requestReader.readLine()) != null && !line.isEmpty()) {
            requestHead.add(line);
        }
        return requestHead;
    }

    private String readBody(int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] body = new char[contentLength];
        requestReader.read(body, 0, contentLength);
        return new String(body);
    }
}
