package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpHeaderConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestDecoder {

    Logger logger = LoggerFactory.getLogger(HttpRequestDecoder.class);

    public HttpRequest decode(BufferedReader bufferedReader) {
        try {
            HttpRequestLine httpRequestLine = HttpRequestLine.decode(bufferedReader.readLine());
            HttpHeader httpHeader = HttpHeaderConverter.decode(bufferedReader);
            String body = decodeBody(httpHeader.getContentLength(), bufferedReader);

            return new HttpRequest(httpRequestLine, httpHeader, body);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalArgumentException();
        }
    }

    private String decodeBody(int contentLength, BufferedReader bufferedReader) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }
}
