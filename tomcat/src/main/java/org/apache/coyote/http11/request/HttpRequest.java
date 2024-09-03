package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.method.HttpMethod;

public class HttpRequest {

    private final RequestLine requestLine;

    public HttpRequest(InputStream inputStream) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            requestLine = readRequestLine(bufferedReader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private RequestLine readRequestLine(BufferedReader reader) throws IOException {
        String line = reader.readLine();
        validateLineEmpty(line);

        return new RequestLine(line);
    }

    private void validateLineEmpty(String line) {
        if (StringUtils.isEmpty(line)) {
            throw new IllegalArgumentException("Line is Empty");
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getRequestURI() {
        return requestLine.getRequestURI();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
    }
}

