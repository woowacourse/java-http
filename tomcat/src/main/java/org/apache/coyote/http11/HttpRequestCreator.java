package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.HttpRequestBody;
import nextstep.jwp.http.reqeust.HttpRequestLine;

public class HttpRequestCreator {

    private static final String HTTP_LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    public static final int REQUEST_LINE_CONTENT_COUNT = 3;

    public HttpRequest createHttpRequest(final BufferedReader bufferReader) throws IOException {
        HttpRequestLine requestLine = httpRequestLine(bufferReader);
        HttpHeader header = httpRequestHeader(bufferReader);
        HttpRequestBody requestBody = httpRequestBody(header, bufferReader);
        return new HttpRequest(requestLine, header, requestBody);
    }

    private HttpRequestLine httpRequestLine(final BufferedReader bufferReader) throws IOException {
        String[] line = bufferReader.readLine().split(HTTP_LINE_SEPARATOR);
        validateRequestLineFormat(line);

        String httpMethod = line[METHOD_INDEX];
        URI httpUri = httpRequestUri(line[URL_INDEX]);
        String httpVersion = line[VERSION_INDEX];

        return new HttpRequestLine(httpMethod, httpUri, httpVersion);
    }

    private void validateRequestLineFormat(final String[] line) {
        if (line.length != REQUEST_LINE_CONTENT_COUNT) {
            throw new IllegalArgumentException();
        }
    }

    private URI httpRequestUri(final String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException();
        }
    }

    private HttpHeader httpRequestHeader(final BufferedReader bufferReader) throws IOException {
        List<String> headers = new ArrayList<>();
        while (bufferReader.readLine().isBlank()) {
            headers.add(bufferReader.readLine());
        }
        return new HttpHeader(headers);
    }

    private HttpRequestBody httpRequestBody(final HttpHeader headers, final BufferedReader bufferReader)
            throws IOException {
        int contentLength = Integer.parseInt(headers.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferReader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        return new HttpRequestBody(requestBody);
    }
}
