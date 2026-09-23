package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeaders;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

public class Http11RequestProcessor {
    private static final char CR = '\r';
    private static final char LF = '\n';
    private final InputStream inputStream;

    public Http11RequestProcessor(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest process() throws IOException {
        RequestLine requestLine = RequestLine.from(readLine());
        HttpHeaders headers = readHeaders();

        return new HttpRequest(requestLine, headers, readBody(headers.getContentLength()));
    }

    private HttpHeaders readHeaders() throws IOException {
        Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String line;

        while (!(line = readLine()).isBlank()) {
            int separatorIndex = line.indexOf(':');
            headers.put(line.substring(0, separatorIndex).strip(), line.substring(separatorIndex + 1).strip());
        }
        return new HttpHeaders(headers);
    }

    private String readLine() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteUnit;
        while ((byteUnit = inputStream.read()) != -1) {
            if (byteUnit != CR) {
                buffer.write(byteUnit);
                continue;
            }
            int next = inputStream.read();
            if (next == LF) {
                return buffer.toString(StandardCharsets.ISO_8859_1);
            }
            buffer.write(byteUnit);
            if (next != -1) {
                buffer.write(next);
            }
        }
        throw new EOFException("각 줄은 CRLF로 끝나야 합니다");
    }

    private byte[] readBody(int contentLength) throws IOException {
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length < contentLength) {
            throw new EOFException("body 길이가 content length와 일치하지 않습니다.");
        }
        return body;
    }
}
