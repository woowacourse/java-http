package org.apache.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.exception.HttpMessageParsingException;

public class HttpRequestMessage {

    private final HttpMethod method;
    private final String uri;
    private final String version;

    public HttpRequestMessage(InputStream inputStream) throws HttpMessageParsingException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            List<String> startLine = List.of(bufferedReader.readLine().split(" "));
            method = HttpMethod.valueOf(startLine.get(0));
            uri = startLine.get(1);
            version = startLine.get(2);
        } catch (IOException | IllegalArgumentException e) {
            throw new HttpMessageParsingException("HTTP 요청 메세지가 올바르지 않습니다.");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }
}
