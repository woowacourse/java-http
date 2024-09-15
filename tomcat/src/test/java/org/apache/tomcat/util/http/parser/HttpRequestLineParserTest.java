package org.apache.tomcat.util.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.tomcat.util.http.HttpMethod;
import org.apache.tomcat.util.http.HttpRequestLine;
import org.apache.tomcat.util.http.HttpVersion;
import org.apache.tomcat.util.http.ResourceURI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestLineParserTest {

    @DisplayName("HTTP RequestRine을 파싱할 수 있다.")
    @Test
    void parse() throws IOException {
        String requestData = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n\r\n";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(requestData));

        HttpRequestLine actual = HttpRequestLineParser.parse(bufferedReader);
        HttpRequestLine expected = new HttpRequestLine(HttpMethod.GET, new ResourceURI("/index.html"), HttpVersion.HTTP11);
        assertThat(actual).isEqualTo(expected);
    }
}
