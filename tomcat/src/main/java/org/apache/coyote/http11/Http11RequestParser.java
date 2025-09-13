package org.apache.coyote.http11;

import static java.net.URLDecoder.decode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11RequestParser {

    private static final Logger log = LoggerFactory.getLogger(Http11RequestParser.class);

    public static Http11Request parse(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // Request Line 파싱
        final RequestLine requestLine = new RequestLine(bufferedReader.readLine());

        // Header 파싱
        final Headers headers = new Headers();
        String headerLine;
        while (!(headerLine = bufferedReader.readLine()).isBlank()) {
            headers.addHeader(headerLine);
        }

        // Request Body 파싱
        final String contentType = headers.getHeader("Content-Type");
        final String contentLengthStr = headers.getHeader("Content-Length");
        RequestBodyParams requestBodyParams = new RequestBodyParams();
        if ((contentLengthStr != null)
                && (contentType != null)
                && (contentType.equals("application/x-www-form-urlencoded"))) {
            // 바디 읽기
            final int contentLength = Integer.parseInt(contentLengthStr);
            char[] bodyChars = new char[contentLength];
            bufferedReader.read(bodyChars, 0, contentLength);
            final String body = new String(bodyChars);
            final String decodedBody = decode(body, StandardCharsets.UTF_8);
            requestBodyParams.parseRequestBodyParams(decodedBody);
        }
        return new Http11Request(requestLine, headers, requestBodyParams);
    }
}
