package org.apache.tomcat.util.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestHeaderParserTest {

    @DisplayName("HttpRequestHeader를 파싱할 수 있다.")
    @Test
    void parse() throws IOException {
        String requestData = "Host: localhost\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: 0\r\n\r\n";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(requestData));

        HttpHeaders actual = HttpRequestHeaderParser.parse(bufferedReader);
        HttpHeaders expected = new HttpHeaders(
                Map.of(new HttpHeaderType("Host"), "localhost",
                        new HttpHeaderType("Content-Type"), "text/html",
                        new HttpHeaderType("Content-Length"), "0"));
        assertThat(actual).isEqualTo(expected);
    }
}
