package org.apache.coyote.http11;

import static java.net.URLDecoder.decode;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

        RequestBodyParams requestBodyParams = new RequestBodyParams();
        if ((contentLengthStr != null)
                && (contentType != null)
                && (contentType.equals("application/x-www-form-urlencoded"))) {
            final int contentLength = Integer.parseInt(contentLengthStr);

            // 바디 읽기
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            final String body = new String(bodyChars);
            final String decodedBody = decode(body, StandardCharsets.UTF_8);
            requestBodyParams.parseRequestBodyParams(decodedBody);
        }
        return new Http11Request(requestLine, headers, requestBodyParams);
    }
}
