package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.HttpMethod;

public class HttpRequest {

    private final RequestStartLine startLine;
    private final RequestHeader header;

    public HttpRequest(final RequestStartLine startLine, final RequestHeader header) {
        this.startLine = startLine;
        this.header = header;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final var startLine = RequestStartLine.from(bufferedReader.readLine());
        final var header = RequestHeader.from(readHeader(bufferedReader));

        return new HttpRequest(startLine, header);
    }

    private static String readHeader(final BufferedReader bufferedReader) throws IOException {
        final var stringBuilder = new StringBuilder();
        var line = "";
        while (!(line = bufferedReader.readLine()).isBlank()) {
            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

    public Map<String, String> parseQueryString() {
        final var parameters = new HashMap<String, String>();
        for (var parameter : getQueryString().split("&")) {
            final var pair = parameter.split("=");
            parameters.put(pair[0], pair[1]);
        }
        return parameters;
    }

    public RequestStartLine getStartLine() {
        return startLine;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public String getQueryString() {
        return startLine.getQueryString();
    }
}
