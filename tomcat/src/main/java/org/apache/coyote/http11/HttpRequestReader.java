package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestReader {

    public HttpRequest readHttpRequest(BufferedReader bufferedReader) throws IOException {
        List<String> lines = readRequestHeaders(bufferedReader);
        RequestLine requestLine = RequestLine.from(lines.get(0));
        HttpHeaders httpHeaders = HttpHeaders.from(lines.subList(1, lines.size()));
        String requestBody = readRequestBody(bufferedReader, httpHeaders.contentLength());
        return new HttpRequest(requestLine, httpHeaders, requestBody);
    }

    public static String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public List<String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = "";
        while (!(line = bufferedReader.readLine()).equals("")) {
            lines.add(line);
        }
        return lines;
    }
}
