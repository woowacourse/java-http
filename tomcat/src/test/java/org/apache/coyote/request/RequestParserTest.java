package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParserTest {

    @Test
    @DisplayName("주어진 InputStream의 정보를 파싱하여 request 객체를 생성한다.")
    void parse_normal() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("GET /index.html HTTP/1.1\r\n".getBytes());
        RequestParser requestParser = new RequestParser(inputStream);

        Request request = requestParser.parse();

        assertThat(request.getPath()).contains("/index.html");
    }

    @Test
    @DisplayName("주어진 url의 쿼리스트링을 파싱해 쿼리스트링을 key와 value로 가지고 있는 request 객체를 생성할 수 있다.")
    void parse_queryString() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(
                "GET /index.html?account=123&password=password1234! HTTP/1.1\r\n".getBytes());
        RequestParser requestParser = new RequestParser(inputStream);

        Request request = requestParser.parse();

        assertAll(
                () -> assertThat(request.getPath()).contains("/index.html"),
                () -> assertThat(request.getQueryString()).containsEntry("account", "123"),
                () -> assertThat(request.getQueryString()).containsEntry("password", "password1234!")
        );
    }
}
