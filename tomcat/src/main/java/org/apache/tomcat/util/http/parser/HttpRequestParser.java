package org.apache.tomcat.util.http.parser;

import java.io.BufferedReader;
import java.io.IOException;

import org.apache.catalina.connector.HttpRequest;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpHeaders;

public class HttpRequestParser {

    private HttpRequestParser() {
    }

    public static HttpRequest parse(BufferedReader bufferedReader) {
        try {
            HttpRequestLine requestLine = HttpRequestLineParser.parse(bufferedReader);
            HttpHeaders headers = HttpRequestHeaderParser.parse(bufferedReader);
            HttpBody body = HttpRequestBodyParser.parse(bufferedReader, headers);
            return new HttpRequest(requestLine, headers, body);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
