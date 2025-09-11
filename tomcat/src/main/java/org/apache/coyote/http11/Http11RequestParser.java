package org.apache.coyote.http11;

import static java.net.URLDecoder.decode;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11RequestParser {

    private static final Logger log = LoggerFactory.getLogger(Http11RequestParser.class);

    public static Http11Request parse(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = new RequestLine(reader.readLine());
        final Headers headers = new Headers();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            headers.addHeader(headerLine);
        }

        // body 파서를 위한 content-type, content-length 헤더 확인
        final String contentType = headers.getHeader("Content-Type");
        final String contentLengthStr = headers.getHeader("Content-Length");

        Map<String, String> bodyParams = new HashMap<>();
        if ((contentLengthStr != null)
                && (contentType != null)
                && (contentType.equals("application/x-www-form-urlencoded"))) {
            final int contentLength = Integer.parseInt(contentLengthStr);

            // 바디 읽기
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            String body = new String(bodyChars);

            // 바디 파싱
            String[] params = body.split("&");
            for (String param : params) {
                String[] pair = param.split("=", 2);
                if (pair.length == 2) {
                    try {
                        // URL 디코딩 후 삽입
                        final String decodedKey = decode(pair[0], StandardCharsets.UTF_8);
                        final String decodedValue = decode(pair[1], StandardCharsets.UTF_8);
                        log.debug("Decoded body param: {}={}", decodedKey, decodedValue);
                        bodyParams.put(decodedKey, decodedValue);
                    } catch (IllegalArgumentException e) {
                        // URL 디코딩 실패 시 원본 삽입
                        log.debug("Body param: {}={}", pair[0], pair[1]);
                        bodyParams.put(pair[0], pair[1]);
                    }
                }
            }
        }
        return new Http11Request(requestLine, headers, bodyParams);
    }
}
