package org.apache.coyote.http11.message.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.message.common.HttpHeader;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    @DisplayName("HttpLine, Header, Body가 올바르게 파싱된다.")
    @Test
    void parseWithHttpLineHeaderBody() throws IOException {
        //given
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password ");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        //when
        HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

        RequestLine requestLine = httpRequest.getRequestLine();
        HttpHeaders headers = httpRequest.getHeaders();
        String body = httpRequest.getBody();

        //then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(requestLine.getUri()).isEqualTo("/login"),
                () -> assertThat(requestLine.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(headers.getValue(HttpHeader.HOST)).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getValue(HttpHeader.CONTENT_LENGTH)).isEqualTo("30"),
                () -> assertThat(body).isEqualTo("account=gugu&password=password")
        );
    }

    @DisplayName("HttpLine이 없으면 에러가 발생한다.")
    @Test
    void parseWithNoHttpLine() {
        String request = String.join("\r\n",
                "Host: localhost:8080 ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password ");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        //when & then
        assertThatThrownBy(() -> HttpRequestParser.parse(inputStream))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Body가 없는 경우 에러가 발생하지 않는다.")
    @Test
    void parseWithNoBody() throws IOException {
        //given
        String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "");

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        //when
        HttpRequest httpRequest = HttpRequestParser.parse(inputStream);

        RequestLine requestLine = httpRequest.getRequestLine();
        HttpHeaders headers = httpRequest.getHeaders();
        String body = httpRequest.getBody();

        //then
        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getUri()).isEqualTo("/login"),
                () -> assertThat(requestLine.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(headers.getValue(HttpHeader.HOST)).isEqualTo("localhost:8080"),
                () -> assertThat(body).isEmpty()
        );
    }
}
