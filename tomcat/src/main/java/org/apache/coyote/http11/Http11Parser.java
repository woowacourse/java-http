package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Http11Parser {

    private static final String REQUEST_HEADER_SUFFIX = "";

    public static Http11Request readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final Http11RequestStartLine startLine = readHttpRequestStartLine(bufferedReader);
        final Http11RequestHeader header = readHttpRequestHeader(bufferedReader);
        final Http11RequestBody body = readHttpRequestBody(bufferedReader, header);
        return new Http11Request(startLine, header, body);
    }

    private static Http11RequestStartLine readHttpRequestStartLine(final BufferedReader bufferedReader)
            throws IOException {
        final String startLine = bufferedReader.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("HttpRequest가 비어있습니다.");
        }
        return Http11RequestStartLineParser.parse(startLine);
    }

    private static Http11RequestHeader readHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!REQUEST_HEADER_SUFFIX.equals(line)) {
            line = bufferedReader.readLine();
            lines.add(line);
        }
        return Http11RequestHeaderParser.parse(lines);
    }

    private static Http11RequestBody readHttpRequestBody(final BufferedReader bufferedReader,
                                                         final Http11RequestHeader header) throws IOException {
        final String contentLengthHeader = header.get("Content-Length");
        if (contentLengthHeader == null) {
            return new Http11RequestBody();
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String encodedBody = new String(buffer);
        return Http11RequestBodyParser.parse(encodedBody);
    }

    public static String writeHttpResponse(final Http11Response response) {
        final StringBuilder serializedResponse = new StringBuilder();
        serializedResponse.append("HTTP/1.1 ").append(response.getStatusCode()).append(" \r\n");
        if (response.getLocation() != null) {
            serializedResponse.append("Location: ").append(response.getLocation()).append(" \r\n");
            serializedResponse.append("Content-Length: 0 \r\n");
            serializedResponse.append("\r\n");
        }
        if (response.getContent() != null) {
            serializedResponse.append("Content-Type: ").append(response.getContentType()).append(";charset=utf-8 \r\n");
            serializedResponse.append("Content-Length: ").append(response.getContentLength()).append(" \r\n");
            serializedResponse.append("\r\n");
            serializedResponse.append(response.getBody());
        }
        return serializedResponse.toString();
    }
}
