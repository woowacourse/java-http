package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpHeaderConverter;

public class HttpRequestDecoder {

    public HttpRequest decode(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        HttpRequestLine httpRequestLine = decodeRequestLine(bufferedReader);
        HttpHeader httpHeader = decodeHeader(bufferedReader);
        String body = decodeBody(httpHeader.getContentLength(), bufferedReader);

        return new HttpRequest(httpRequestLine, httpHeader, body);
    }

    private HttpRequestLine decodeRequestLine(BufferedReader bufferedReader) {
        try {
            return HttpRequestLine.decode(bufferedReader.readLine());
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private HttpHeader decodeHeader(BufferedReader bufferedReader) {
        return HttpHeaderConverter.decode(bufferedReader);
    }

    private String decodeBody(int contentLength, BufferedReader bufferedReader) {
        char[] buffer = new char[contentLength];
        try {
            bufferedReader.read(buffer, 0, contentLength);
            return new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
