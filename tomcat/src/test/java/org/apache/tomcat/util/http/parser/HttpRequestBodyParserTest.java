package org.apache.tomcat.util.http.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

import org.apache.tomcat.util.http.body.HttpBody;
import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestBodyParserTest {

    @DisplayName("HttpRequestBody를 파싱할 수 있다.")
    @Test
    void parse() throws IOException {
        String requestBody = "This is the body of the request";

        BufferedReader bufferedReader = new BufferedReader(new StringReader(requestBody));

        HttpHeaders headers = new HttpHeaders(Map.of(HttpHeaderType.CONTENT_LENGTH, String.valueOf(requestBody.length())));
        HttpBody actual = HttpRequestBodyParser.parse(bufferedReader, headers);
        HttpBody expected = new HttpBody("This is the body of the request");

        assertThat(actual).isEqualTo(expected);
    }
}
