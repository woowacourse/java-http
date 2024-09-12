package org.apache.tomcat.util.http.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.StringReader;

import org.apache.catalina.connector.HttpRequest;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {
    @DisplayName("Http Request 로 파싱할 수 있다.")
    @Test
    void parse() {
        String requestData = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n\r\n";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(requestData));

        HttpRequest httpRequest = HttpRequestParser.parse(bufferedReader);

        HttpRequestLine requestLine = httpRequest.requestLine();

        assertAll(
                () -> assertThat("GET").isEqualTo(requestLine.httpMethod().name()),
                () -> assertThat("/index.html").isEqualTo(requestLine.resourceURI().uri()),
                () -> assertThat("HTTP/1.1").isEqualTo(requestLine.httpVersion().getVersion())
        );

        HttpHeaders headers = httpRequest.httpHeaders();
        assertAll(
                () -> assertThat("localhost").isEqualTo(headers.get(new HttpHeaderType("Host"))),
                () -> assertThat("text/html").isEqualTo(headers.get(new HttpHeaderType("Content-Type"))),
                () -> assertThat("0").isEqualTo(headers.get(new HttpHeaderType("Content-Length")))
        );

        HttpBody body = httpRequest.httpBody();
        assertThat("").isEqualTo(body.body());
    }
}
