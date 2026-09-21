package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final char HEADER_PARAM_DELIMITER = ':';
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final HttpBody httpBody;

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        this.requestLine = new RequestLine(bufferedReader.readLine());
        this.httpHeaders = requestHeaders(bufferedReader);
        this.httpBody = requestBody(bufferedReader);
    }

    private HttpHeaders requestHeaders(BufferedReader bufferedReader) throws IOException {
        HttpHeaders headers = new HttpHeaders();

        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            // 가장 왼쪽의 콜론을 기준으로 파싱한다.
            final int firstColonIndex = line.indexOf(HEADER_PARAM_DELIMITER);
            if (firstColonIndex == -1) {
                throw new IllegalArgumentException("헤더 포맷이 잘못되었습니다.");
            }

            final String key = line.substring(0, firstColonIndex).strip();
            final String value = line.substring(firstColonIndex + 1).strip();
            headers.put(key, value);
        }
        return headers;
    }

    private HttpBody requestBody(BufferedReader bufferedReader) throws IOException {
        String contentLengthString = httpHeaders.get(CONTENT_LENGTH);
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
        if (contentLength == 0) {
            return new HttpBody(new String(""));
        }
        throw new IOException("Content-Length는 음수일 수 없습니다.");
    }

    public boolean hasSamePath(String path) {
        return requestLine.hasSamePath(path);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public HttpCookie getCookies() {
        return new HttpCookie(httpHeaders.get("Cookie"));
    }

    public Map<String, String> getParameters() {
        String queryLine = httpBody.getValue();
        final String[] params = queryLine.split(QUERY_PARAM_DELIMITER);

        final Map<String, String> queries = new HashMap<>();
        for (String param : params) {
            final String[] keyToken = param.split(KEY_VALUE_DELIMITER);
            queries.put(URLDecoder.decode(keyToken[0], StandardCharsets.UTF_8),
                    URLDecoder.decode(keyToken[1], StandardCharsets.UTF_8));
        }
        return queries;
    }
}
