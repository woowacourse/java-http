package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestReaderTest {

    @DisplayName("GET 요청 생성 테스트")
    @Test
    void acceptTest() throws IOException {
        // given
        BufferedReader reader = new BufferedReader(new StringReader(
                """
                        GET /simple?page=1&offset=2 HTTP/1.1
                        Host: localhost:8080
                        Accept: text/html
                        Content-Length: 0
                        Cookie: JSESSIONID=1234567890; hello=world
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9

                        """));

        // when
        HttpRequest request = HttpRequestReader.accept(reader);

        // then
        assertAll(
                () -> assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getUri()).isEqualTo("/simple"),
                () -> assertThat(request.getHeader().getContentLength()).isEqualTo(0),
                () -> assertThat(request.getParameter("page")).isEqualTo("1"),
                () -> assertThat(request.getParameter("offset")).isEqualTo("2"),
                () -> assertThat(request.getCookie().get("JSESSIONID").get()).isEqualTo("1234567890"),
                () -> assertThat(request.getCookie().get("hello").get()).isEqualTo("world")
        );
    }

    @DisplayName("POST 요청 생성 테스트")
    @Test
    void acceptTest1() throws IOException {
        // given
        BufferedReader reader = new BufferedReader(new StringReader(
                """
                        POST /login HTTP/1.1
                        Host: localhost:8080
                        Accept: text/html
                        Content-Length: 24
                        Cookie: JSESSIONID=1234567890; hello=world
                        Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
                                                
                        id=mangcho&password=1234
                        """));

        // when
        HttpRequest request = HttpRequestReader.accept(reader);

        // then
        assertAll(
                () -> assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(request.getUri()).isEqualTo("/login"),
                () -> assertThat(request.getHeader().getContentLength()).isEqualTo(24),
                () -> assertThat(request.getCookie().get("JSESSIONID").get()).isEqualTo("1234567890"),
                () -> assertThat(request.getCookie().get("hello").get()).isEqualTo("world"),
                () -> assertThat(request.getBody().getValue("id")).isEqualTo("mangcho"),
                () -> assertThat(request.getBody().getValue("password")).isEqualTo("1234")
        );
    }
}
