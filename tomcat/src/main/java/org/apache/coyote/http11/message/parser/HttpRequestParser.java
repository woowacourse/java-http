package org.apache.coyote.http11.message.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestLine;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        RequestLine requestLine = parseHttpLine(bufferedReader);
        HttpHeaders headers = parseHttpHeaders(bufferedReader);
        String body = parseBody(bufferedReader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private static RequestLine parseHttpLine(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("HTTP line은 null일 수 없습니다.");
        }
        return RequestLineParser.parse(line);
    }

    private static HttpHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            headerLines.add(line);
        }
        return HttpHeadersParser.parse(headerLines);
    }

    private static String parseBody(BufferedReader bufferedReader, HttpHeaders headers) throws IOException {
        int contentLength = headers.getContentLength();
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
