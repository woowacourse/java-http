package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.coyote.http11.common.HttpHeaders;

public class HttpRequestFactory {

    private HttpRequestFactory() {
    }

    public static HttpRequest create(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);

        String line = reader.readLine();
        if (line == null) {
            throw new IOException("Empty request");
        }

        RequestLine requestLine = RequestLine.from(line);
        HttpHeaders headers = HttpHeaders.from(readHeaders(reader));
        RequestBody requestBody = RequestBody.from(readRequestBody(reader, headers));

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static List<String> readHeaders(BufferedReader reader) {
        return reader.lines()
                .takeWhile(s -> !s.isBlank())
                .toList();
    }

    private static String readRequestBody(BufferedReader reader, HttpHeaders httpHeaders) throws IOException {
        int contentLength = httpHeaders.getContentLength();

        char[] buffer = new char[contentLength];
        int read = reader.read(buffer, 0, contentLength);

        return new String(buffer, 0, read);
    }
}
