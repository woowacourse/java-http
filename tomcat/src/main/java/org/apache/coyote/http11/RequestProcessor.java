package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.requestLine.HttpRequestLine;

public class RequestProcessor {

    private static final String HEADER_END = "";

    public void process(InputStream inputStream, HttpRequest request) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestLine httpRequestLine = HttpRequestLine.toHttpRequestLine(br.readLine());
        HttpRequestHeader httpRequestHeader = getHttpRequestHeader(br);

        HttpRequestBody httpRequestBody = null;
        if (httpRequestLine.getMethodType().isPost()) {
            httpRequestBody = getHttpRequestBody(httpRequestHeader, br);
        }

        request.setHttpRequestLine(httpRequestLine);
        request.setHttpRequestHeader(httpRequestHeader);
        request.setHttpRequestBody(httpRequestBody);
    }

    private HttpRequestHeader getHttpRequestHeader(BufferedReader br) throws IOException {
        List<String> requestHeaderLines = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null && !line.equals(HEADER_END)) {
            requestHeaderLines.add(line);
        }
        return HttpRequestHeader.toHttpRequestHeader(requestHeaderLines);
    }

    private HttpRequestBody getHttpRequestBody(HttpRequestHeader httpRequestHeader, BufferedReader br)
            throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getContentLength());
        char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return HttpRequestBody.toHttpRequestBody(requestBody, httpRequestHeader.getContentType());
    }
}
