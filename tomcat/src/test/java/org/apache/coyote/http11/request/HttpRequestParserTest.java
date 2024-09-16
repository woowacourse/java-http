package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HttpRequestParserTest {

    @Test
    @DisplayName("Request의 첫 줄이 올바르지 않은 형식으로 입력되면 오류가 발생한다.")
    void should_throw_exception_when_invalid_request_requested() {
        // given
        String request = String.join("\r\n",
                "GET /index.html",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.getInstance().parseRequest(bufferedReader));
    }

    @Test
    @DisplayName("존재하지 않는 HttpMethod를 입력하면 오류가 발생한다.")
    void should_throw_exception_when_invalid_http_method_requested() {
        String request = String.join("\r\n",
                "GETS /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> HttpRequestParser.getInstance().parseRequest(bufferedReader));
    }

    @Test
    @DisplayName("QueryString의 구분자가 잘못 입력하면 빈 QueryString 객체를 반환한다.")
    void should_return_empty_queryString_when_delimiter_invalid() throws IOException {
        //given
        String request = String.join("\r\n",
                "GET /index&account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        //when
        HttpRequest httpRequest = HttpRequestParser.getInstance().parseRequest(bufferedReader);
        QueryString queryString = httpRequest.getQueryString();

        //then
        assertThat(queryString.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("QueryString의 구분자 이후 내용이 없으면 오류가 발생한다.")
    void should_throw_exception_when_empty_string_after_delimiter() throws IOException {
        //given
        String request = String.join("\r\n",
                "GET /index? HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        //when & then
        assertThrows(StringIndexOutOfBoundsException.class, () -> HttpRequestParser.getInstance().parseRequest(bufferedReader));
    }

    @Test
    @DisplayName("Header의 내용이 없으면 빈 HttpRequestHeaders를 반환한다.")
    void should_return_empty_header_when_request_header_line_empty() throws IOException {
        //given
        String request = String.join("\r\n",
                "GET /index HTTP/1.1",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        //when
        HttpRequest httpRequest = HttpRequestParser.getInstance().parseRequest(bufferedReader);
        HttpRequestHeaders headers = httpRequest.getHttpRequestHeaders();

        //then
        assertThat(headers.isEmpty()).isTrue();
    }
}
