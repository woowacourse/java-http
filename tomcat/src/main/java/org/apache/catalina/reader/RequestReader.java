package org.apache.catalina.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestReader {

    private static final Logger log = LoggerFactory.getLogger(RequestReader.class);
    private final BufferedReader reader;

    public RequestReader(BufferedReader reader) {
        this.reader = reader;
    }

    public List<String> readRequest() {
        List<String> readRequest = new ArrayList<>();
        try {
            String line;
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                readRequest.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("요청 헤더를 읽는 도중 오류가 발생했습니다.");
        }
        validateRequestLines(readRequest);
        return readRequest;
    }

    private void validateRequestLines(List<String> headerLines) {
        if (headerLines.isEmpty()) {
            throw new IllegalArgumentException("요청 헤더가 비어 있습니다.");
        }
    }

    public String readBody(int contentLength) {
        char[] buffer = new char[contentLength];
        try {
            int readChars = reader.read(buffer, 0, contentLength);
            if (readChars < contentLength) {
                throw new IllegalArgumentException("실제 읽은 바이트 수가 예상된 길이보다 작습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException("요청 본문을 읽는 도중 오류가 발생했습니다.");
        }
        return new String(buffer);
    }
}
