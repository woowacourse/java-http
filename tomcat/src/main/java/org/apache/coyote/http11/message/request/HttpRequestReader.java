package org.apache.coyote.http11.message.request;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.message.HttpHeaders;
import org.apache.coyote.http11.message.HttpHeadersParser;

public class HttpRequestReader {

    private static final Byte CR = '\r';
    private static final Byte LF = '\n';
    private static final byte[] EMPTY_BODY = new byte[0];

    private final InputStream inputStream;

    public HttpRequestReader(InputStream inputStream) {
        this.inputStream = new BufferedInputStream(inputStream);
    }

    public HttpRequest read() throws IOException {
        String requestLine = readLine();
        HttpHeaders headers = readHeaderLines();
        byte[] bodyBytes = readBody(headers);
        HttpRequestBody body = new HttpRequestBody(bodyBytes, HttpBodyParser.parseToFormParameters(bodyBytes, headers));

        return HttpRequest.of(requestLine, headers, body);
    }

    private HttpHeaders readHeaderLines() throws IOException {
        List<String> headerLines = new ArrayList<>();

        String headerLine = readLine();
        while (StringUtils.isNotBlank(headerLine)) {
            headerLines.add(headerLine);
            headerLine = readLine();
        }

        return HttpHeadersParser.parseToHeaders(headerLines);
    }

    private String readLine() throws IOException {
        List<Byte> line = new ArrayList<>();
        int byteValue = inputStream.read();

        while (byteValue != -1 && isNotEndOfLine(byteValue)) {
            line.add((byte) byteValue);
            byteValue = inputStream.read();
        }

        return toString(line);
    }

    private boolean isNotEndOfLine(int token) throws IOException {
        if (token != CR) {
            return true;
        }
        if (inputStream.read() == LF) {
            return false;
        }

        throw new IllegalArgumentException("요청 라인의 끝은 \\r\\n으로 끝나야 합니다.");
    }

    private String toString(List<Byte> line) {
        Byte[] byteArray = line.toArray(new Byte[0]);
        return new String(ArrayUtils.toPrimitive(byteArray));
    }

    private byte[] readBody(HttpHeaders headers) {
        return headers.getFieldByHeaderName("Content-Length")
                .map(Integer::parseInt)
                .map(this::readBody)
                .orElse(EMPTY_BODY);
    }

    private byte[] readBody(int contentLength) {
        byte[] bodyBytes;

        try {
            bodyBytes = inputStream.readNBytes(contentLength);
        } catch (IOException e) {
            throw new IllegalArgumentException("HTTP body를 읽는데 실패하였습니다.", e);
        }

        if (bodyBytes.length != contentLength) {
            throw new IllegalArgumentException("HTTP body의 길이와 Content-Type 의 값이 일치하지 않습니다.");
        }
        return bodyBytes;
    }
}
