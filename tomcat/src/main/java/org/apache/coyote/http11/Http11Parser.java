package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import org.apache.coyote.HttpCookies;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpRequestBody;
import org.apache.coyote.HttpRequestHeader;
import org.apache.coyote.HttpRequestStartLine;
import org.apache.coyote.HttpResponse;

public class Http11Parser { // TODO: 각 객체 안으로 넣어줄지 고민하기

    private static final String REQUEST_HEADER_SUFFIX = "";

    public static HttpRequest readHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestStartLine startLine = readHttpRequestStartLine(bufferedReader);
        final HttpRequestHeader header = readHttpRequestHeader(bufferedReader);
        final HttpCookies cookie = readHttpRequestCookie(header);
        final HttpRequestBody body = readHttpRequestBody(bufferedReader, header);
        return new HttpRequest(startLine, header, cookie, body);
    }

    private static HttpRequestStartLine readHttpRequestStartLine(final BufferedReader bufferedReader)
            throws IOException {
        final String startLine = bufferedReader.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IllegalArgumentException("HttpRequest가 비어있습니다.");
        }
        return Http11RequestStartLineParser.parse(startLine);
    }

    private static HttpRequestHeader readHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!REQUEST_HEADER_SUFFIX.equals(line)) {
            line = bufferedReader.readLine();
            lines.add(line);
        }
        return Http11RequestHeaderParser.parse(lines);
    }

    private static HttpCookies readHttpRequestCookie(final HttpRequestHeader header) {
        final String cookieHeader = header.get("Cookie");
        if (cookieHeader == null) {
            return new HttpCookies();
        }
        return Http11CookieParser.parse(cookieHeader);
    }

    private static HttpRequestBody readHttpRequestBody(final BufferedReader bufferedReader,
                                                       final HttpRequestHeader header) throws IOException {
        final String contentLengthHeader = header.get("Content-Length");
        if (contentLengthHeader == null) {
            return new HttpRequestBody();
        }
        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String encodedBody = new String(buffer);
        return Http11RequestBodyParser.parse(encodedBody);
    }

    public static String writeHttpResponse(final HttpResponse response) {
        final StringBuilder serializedResponse = new StringBuilder();
        serializedResponse.append("HTTP/1.1 ").append(response.getStatusCode()).append(" \r\n");
        for (Entry<String, String> entry : response.getHeader().entrySet()) {
            serializedResponse.append("%s: %s \n".formatted(entry.getKey(), entry.getValue()));
        }
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
