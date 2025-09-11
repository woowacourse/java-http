package org.apache.coyote.httpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.httpRequest.httpBody.HttpBody;
import org.apache.coyote.httpRequest.httpHeader.ContentType;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;

public class HttpRequest {

    private final HttpHeader httpHeader;
    private final HttpBody httpBody;

    public static HttpRequest createErrorRequest(final String errorPath) {
        return new HttpRequest(HttpHeader.createErrorHttpHeader(errorPath), null);
    }

    public HttpRequest(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.httpHeader = readHttpHeader(bufferedReader);
        this.httpBody = readHttpBody(bufferedReader, httpHeader);
    }

    private HttpRequest(
            final HttpHeader httpHeader,
            final HttpBody httpBody
    ) {
        this.httpHeader = httpHeader;
        this.httpBody = httpBody;
    }

    private HttpHeader readHttpHeader(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final List<String> headers = new ArrayList<>();
        String headerLine;
        while ((headerLine = bufferedReader.readLine()) != null && !headerLine.isEmpty()) {
            headers.add(headerLine);
        }

        return new HttpHeader(requestLine, headers);
    }

    private HttpBody readHttpBody(
            final BufferedReader bufferedReader,
            final HttpHeader httpHeader
    ) throws IOException {
        final String contentLengthValue = httpHeader.getHeader("Content-Length");
        if (contentLengthValue == null) {
            return null;
        }
        final String contentType = httpHeader.getHeader("Content-Type");
        final int contentLength = Integer.parseInt(contentLengthValue);
        final char[] chars = new char[contentLength];
        bufferedReader.read(chars, 0, contentLength);
        final String body = new String(chars);
        final String decodedBody = URLDecoder.decode(body, StandardCharsets.UTF_8);

        return new HttpBody(decodedBody, ContentType.findContentType(contentType));
    }

    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

    public HttpBody getHttpBody() {
        return httpBody;
    }
}
