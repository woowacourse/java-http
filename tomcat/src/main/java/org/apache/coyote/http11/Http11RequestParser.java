package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Http11RequestParser {

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
                    bodyParams.put(pair[0], pair[1]);
                }
            }
        }
        return new Http11Request(requestLine, headers, bodyParams);
    }
}
