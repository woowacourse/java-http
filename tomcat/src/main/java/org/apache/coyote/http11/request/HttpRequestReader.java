package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpHeaders;

public class HttpRequestReader {

    public HttpRequest read(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader =
                new InputStreamReader(inputStream, StandardCharsets.UTF_8);

        BufferedReader reader = new BufferedReader(inputStreamReader);

        String requestLine = reader.readLine();

        if (requestLine == null) {
            return null;
        }

        HttpHeaders headers = readHeaders(reader);
        HttpRequestBody body = readBody(reader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        while (true) {
            String line = reader.readLine();

            if (line == null) {
                throw new IOException("헤더를 읽는 중 입력이 끝났습니다.");
            }

            if (line.isEmpty()) {
                return new HttpHeaders(headerLines);
            }

            headerLines.add(line);
        }
    }

    private HttpRequestBody readBody(
            BufferedReader reader,
            HttpHeaders headers
    ) throws IOException {
        int contentLength = headers.getContentLength();

        char[] body = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int count = reader.read(
                    body,
                    totalRead,
                    contentLength - totalRead
            );

            if (count == -1) {
                throw new IOException("요청 본문이 끝까지 도착하지 않았습니다.");
            }

            totalRead += count;
        }

        return new HttpRequestBody(new String(body));
    }
}
