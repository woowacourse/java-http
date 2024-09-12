package org.apache.tomcat.util.http.parser;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;

class HttpRequestBodyParser {

    private HttpRequestBodyParser() {
    }

    protected static HttpBody parse(BufferedReader bufferedReader, HttpHeaders headers) throws IOException {
        StringBuilder body = new StringBuilder();
        if (headers.contains(HttpHeaderType.CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(headers.get(HttpHeaderType.CONTENT_LENGTH));
            char[] bodyChars = new char[contentLength];
            int bytesRead = bufferedReader.read(bodyChars, 0, contentLength);
            body.append(bodyChars, 0, bytesRead);
        }
        return new HttpBody(body.toString());
    }
}
