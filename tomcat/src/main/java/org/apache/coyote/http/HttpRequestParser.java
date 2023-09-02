package org.apache.coyote.http;

import org.apache.coyote.util.ByteUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser {

    private static final String HEADER_BODY_DELIMITER = "\r\n\r\n";
    private static final int MAXIMUM_HEADER_BUFFER_LEN = 8192;

    public HttpRequest parse(InputStream inputStream) {
        HttpHeaders httpHeaders = parseHeader(inputStream);
        HttpMessage httpMessage = parseBody(httpHeaders, inputStream);

        return new HttpRequest(httpHeaders, httpMessage);
    }

    private HttpHeaders parseHeader(InputStream inputStream) {
        byte[] source = new byte[MAXIMUM_HEADER_BUFFER_LEN];
        byte[] target = HEADER_BODY_DELIMITER.getBytes(StandardCharsets.UTF_8);
        int sourceLength = ByteUtil.readStreamUntilEndsWith(inputStream, source, target);

        return HttpHeaders.from(new String(source, 0, sourceLength));
    }

    private HttpMessage parseBody(HttpHeaders httpHeaders, InputStream inputStream) {
        int contentLength = httpHeaders.getContentLength();
        if (contentLength == 0) {
            return null;
        }
        byte[] source = new byte[contentLength];
        ByteUtil.readStreamOfLength(inputStream, source, contentLength);

        return HttpMessage.of(source, httpHeaders.getContentType());
    }
}
