package org.apache.coyote.http11.request;

import static org.apache.catalina.utils.IOUtils.readData;
import static org.apache.catalina.utils.Parser.removeBlank;
import static org.apache.coyote.http11.header.HttpHeaderType.CONTENT_LENGTH;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.header.HttpHeaderType;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String path;
    private final HttpVersion httpVersion;
    private final Map<HttpHeaderType, HttpHeader> headers;
    private final String body;

    public HttpRequest(final HttpMethod httpMethod,
                       final String path,
                       final HttpVersion httpVersion,
                       final Map<HttpHeaderType, HttpHeader> headers,
                       final String body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(final BufferedReader bufferedReader) throws IOException {
        final String startLine = bufferedReader.readLine();
        if (startLine == null) {
            throw new IllegalArgumentException("request가 비어있습니다.");
        }

        final String[] startLineInfos = startLine.split(" ");
        final HttpMethod method = HttpMethod.of(startLineInfos[0]);
        final String path = startLineInfos[1];
        final HttpVersion version = HttpVersion.of(startLineInfos[2]);
        final Map<HttpHeaderType, HttpHeader> headers = readAllHeaders(bufferedReader);
        final String body = readBody(bufferedReader, headers);

        return new HttpRequest(method, path, version, headers, body);
    }

    private static Map<HttpHeaderType, HttpHeader> readAllHeaders(final BufferedReader bufferedReader) throws IOException {
        final Map<HttpHeaderType, HttpHeader> headers = new LinkedHashMap<>();

        while (true) {
            final String line = bufferedReader.readLine();
            if (line.equals("")) {
                break;
            }
            final String[] header = line.split(":");
            final String headerType = removeBlank(header[0]);
            final String headerValue = removeBlank(header[1]);
            final HttpHeaderType httpHeaderType = HttpHeaderType.of(headerType);
            headers.put(httpHeaderType, HttpHeader.of(httpHeaderType, headerValue));
        }

        return headers;
    }

    private static String readBody(final BufferedReader bufferedReader,
                                   final Map<HttpHeaderType, HttpHeader> headers) throws IOException {

        if (!headers.containsKey(CONTENT_LENGTH)) {
            return "";
        }

        final int contentLength = convertIntFromContentLength(headers.get(CONTENT_LENGTH));
        return readData(bufferedReader, contentLength);
    }

    private static int convertIntFromContentLength(final HttpHeader contentLength) {
        return Integer.parseInt(String.join("", contentLength.getValues()));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Map<HttpHeaderType, HttpHeader> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
