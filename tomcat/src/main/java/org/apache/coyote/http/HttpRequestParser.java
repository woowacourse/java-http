package org.apache.coyote.http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import common.ContentType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeader;
import org.apache.coyote.http.request.HttpRequestLine;

@RequiredArgsConstructor
public class HttpRequestParser {

    private static final HttpRequestParser INSTANCE = new HttpRequestParser();

    public static HttpRequestParser getInstance() {
        return INSTANCE;
    }

    public HttpRequest parse(final InputStream inputStream) throws IOException {
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        final HttpRequestLine requestLine = parseRequestLine(bufferedInputStream);
        final HttpRequestHeader requestHeader = parseHeaders(bufferedInputStream);
        final HttpRequestBody requestBody = parseBody(bufferedInputStream, requestHeader);

        return HttpRequest.from(requestLine, requestHeader, requestBody);
    }

    private HttpRequestLine parseRequestLine(final BufferedInputStream inputStream) throws IOException {
        final String raw = readUntilTerminator(inputStream, new byte[]{'\r', '\n'});
        return HttpRequestLine.from(raw);
    }

    private HttpRequestHeader parseHeaders(final BufferedInputStream inputStream) throws IOException {
        final String raw = readUntilTerminator(inputStream, new byte[]{'\r', '\n', '\r', '\n'});
        return HttpRequestHeader.from(raw);
    }

    private String readUntilTerminator(final InputStream in, final byte[] terminator) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);

        final byte[] recentBytes = new byte[terminator.length];
        int bytesRead = 0;

        int b;
        while ((b = in.read()) != -1) {
            buffer.write(b);
            recentBytes[bytesRead % recentBytes.length] = (byte) b;
            bytesRead++;

            if (matchesTerminator(recentBytes, bytesRead, terminator)) {
                break;
            }
        }

        return buffer.toString(StandardCharsets.ISO_8859_1);
    }

    private boolean matchesTerminator(
            final byte[] recentBytes,
            final int bytesRead,
            final byte[] terminator
    ) {
        if (bytesRead < terminator.length) {
            return false;
        }
        for (int i = 0; i < terminator.length; i++) {
            final int windowIndex = (bytesRead - terminator.length + i) % recentBytes.length;
            if (recentBytes[windowIndex] != terminator[i]) {
                return false;
            }
        }
        return true;
    }

    private HttpRequestBody parseBody(
            final InputStream inputStream,
            final HttpRequestHeader header
    ) throws IOException {
        final int contentLength = header.getContentLength();
        final ContentType contentType = header.getContentType();

        if (contentLength <= 0) {
            return HttpRequestBody.empty();
        }

        final byte[] bodyBytes = inputStream.readNBytes(contentLength);
        if (bodyBytes.length != contentLength) {
            throw new IOException(
                    "Content-Length와 실제 데이터 길이 불일치: 예상=" + contentLength + ", 실제=" + bodyBytes.length);
        }

        final String bodyString = new String(bodyBytes, contentType.getDefaultCharset()); // TODO client request charset

        return HttpRequestBody.from(bodyString, contentType);
    }
}
