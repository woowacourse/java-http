package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {
    public HttpRequest parse(BufferedReader bufferedReader) throws IOException {
        RequestLine requestLine = new RequestLine(bufferedReader.readLine());
        HttpHeaders httpHeaders = requestHeaders(bufferedReader);
        HttpBody httpBody = requestBody(httpHeaders.get("Content-Length"), bufferedReader);

        return new HttpRequest(requestLine, httpHeaders, httpBody);
    }

    private HttpHeaders requestHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            // 가장 왼쪽의 콜론을 기준으로 파싱한다.
            final int firstColonIndex = line.indexOf(':');
            if (firstColonIndex == -1) {
                throw new IllegalArgumentException("헤더 포맷이 잘못되었습니다.");
            }

            final String key = line.substring(0, firstColonIndex).strip();
            final String value = line.substring(firstColonIndex + 1).strip();
            headers.put(key, value);
        }
        return headers;
    }

    private HttpBody requestBody(String contentLengthString, BufferedReader bufferedReader) throws IOException {
        if (contentLengthString == null) {
            return new HttpBody("");
        }
        final int contentLength = Integer.parseInt(contentLengthString);

        if (contentLength > 0) {
            final char[] buffer = new char[contentLength];
            int totalRead = 0;

            while (totalRead < contentLength) {
                final int read = bufferedReader.read(buffer, totalRead, contentLength - totalRead);
                if (read == -1) {
                    throw new IOException("Content-Length와 Body 길이가 일치하지 않습니다.");
                }
                totalRead += read;
            }

            return new HttpBody(new String(buffer));
        }
        return new HttpBody("");
    }
}
