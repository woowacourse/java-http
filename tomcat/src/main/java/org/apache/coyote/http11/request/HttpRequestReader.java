package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.catalina.session.SessionManager;

public class HttpRequestReader {
    private final Set<HttpMethod> supportedMethods;
    private final SessionManager sessionManager;

    public HttpRequestReader(Set<HttpMethod> supportedMethods, SessionManager sessionManager) {
        this.supportedMethods = supportedMethods;
        this.sessionManager = sessionManager;
    }

    public HttpRequest read(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 존재하지 않습니다.");
        }

        HttpHeaders headers = readHeaders(reader);
        HttpBody body = readBody(reader, headers.getContentLength());

        return HttpRequest.from(requestLine, headers, body, supportedMethods, sessionManager);
    }

    private HttpHeaders readHeaders(BufferedReader reader) throws IOException {
        List<String> headerLines = new ArrayList<>();

        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            headerLines.add(line);
            line = reader.readLine();
        }

        if (line == null) {
            throw new IOException("HTTP 헤더가 빈 줄로 끝나지 않았습니다.");
        }

        return HttpHeaders.from(headerLines);
    }

    private HttpBody readBody(BufferedReader reader, int contentLength) throws IOException {
        if (contentLength == 0) {
            return HttpBody.empty();
        }

        char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("HTTP body가 Content-Length보다 짧습니다.");
            }
            totalRead += read;
        }

        return new HttpBody(new String(buffer));
    }
}
