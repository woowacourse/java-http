package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.RequestBody;
import org.apache.coyote.http.RequestLine;

public class Http11RequestParser {

    private final BufferedReader reader;

    public Http11RequestParser(InputStream inputStream) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
    }

    public HttpRequest parse() throws IOException {
        final RequestLine requestLine = RequestLine.from(readFirstLine());
        final HttpHeaders headers = HttpHeaders.from(readHeaderLines());
        final RequestBody body = RequestBody.of(ContentType.from(headers.contentType()), readBody(headers.contentLength()));

        return new HttpRequest(requestLine, headers, body);
    }

    private String readFirstLine() throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            throw new EOFException("요청 데이터 없이 연결이 종료되었습니다.");
        }
        return line;
    }

    private List<String> readHeaderLines() throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }
        if (line == null) {
            throw new EOFException("헤더를 읽는 도중 연결이 종료되었습니다.");
        }
        return lines;
    }

    private String readBody(int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }
        final char[] buffer = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            final int read = reader.read(buffer, offset, contentLength - offset);
            if (read == -1) {
                throw new EOFException("바디를 읽는 도중 연결이 종료되었습니다.");
            }
            offset += read;
        }
        return decodeBody(buffer);
    }

    // ISO-8859-1은 1byte = 1char이므로 원래 바이트로 되돌린 뒤 UTF-8로 해석
    private String decodeBody(char[] buffer) {
        final byte[] bytes = new String(buffer).getBytes(StandardCharsets.ISO_8859_1);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
