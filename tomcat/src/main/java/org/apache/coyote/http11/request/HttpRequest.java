package org.apache.coyote.http11.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;

@Getter
@RequiredArgsConstructor
public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpRequestStartLine httpRequestStartLine;
    private final HttpRequestHeader httpRequestHeader;
    private final HttpRequestBody httpRequestBody;

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        HttpRequestStartLine httpRequestStartLine = parseRequestStartLine(bufferedReader);
        HttpRequestHeader httpRequestHeader = parseRequestHeader(bufferedReader);
        HttpRequestBody httpRequestBody = parseRequestBody(httpRequestHeader.contentLength(), bufferedReader);
        return new HttpRequest(httpRequestStartLine, httpRequestHeader, httpRequestBody);
    }

    private static HttpRequestStartLine parseRequestStartLine(BufferedReader bufferedReader) throws IOException {
        String requestStartLine = bufferedReader.readLine();
        if (requestStartLine == null) {
            log.info("잘못된 형태의 요청!");
            throw new IllegalArgumentException();
        }
        return HttpRequestStartLine.from(requestStartLine);
    }

    private static HttpRequestHeader parseRequestHeader(BufferedReader bufferedReader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            stringBuilder.append(line).append("\r\n");
            line = bufferedReader.readLine();
        }
        return HttpRequestHeader.from(stringBuilder.toString());
    }

    private static HttpRequestBody parseRequestBody(String contentLength, BufferedReader bufferedReader) throws IOException {
        if (contentLength == null) {
            return HttpRequestBody.none();
        }
        int length = Integer.parseInt(contentLength);
        char[] httpRequestBody = new char[length];
        bufferedReader.read(httpRequestBody, 0, length);
        return HttpRequestBody.from(new String(httpRequestBody));
    }
}
