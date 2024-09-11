package org.apache.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestReaderTest {

    @Test
    @DisplayName("GET 요청 읽기 테스트")
    void readGetRequest() throws IOException {
        // given
        String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        HttpRequest request = HttpRequestReader.readHttpRequest(reader);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(request.getHeaders()).hasSize(2),
                () -> assertThat(request.getHeader("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(request.getBody()).isNull()
        );
    }

    @Test
    @DisplayName("POST 요청 읽기 테스트")
    void readPostRequest() throws IOException {
        // given
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 27",
                "",
                "username=john&password=pass");
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        HttpRequest request = HttpRequestReader.readHttpRequest(reader);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(request.getPath()).isEqualTo("/login"),
                () -> assertThat(request.getVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(request.getHeaders()).hasSize(3),
                () -> assertThat(request.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(request.getBody()).isEqualTo("username=john&password=pass")
        );
    }

    @Test
    @DisplayName("헤더가 없는 요청 읽기 테스트")
    void readRequestWithoutHeaders() throws IOException {
        // given
        String rawRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "",
                "");
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        HttpRequest request = HttpRequestReader.readHttpRequest(reader);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo("/"),
                () -> assertThat(request.getVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(request.getHeaders()).isEmpty(),
                () -> assertThat(request.getBody()).isNull()

        );
    }

    @Test
    @DisplayName("Content-Length가 없는 POST 요청 읽기 테스트")
    void readPostRequestWithoutContentLength() throws IOException {
        // given
        String rawRequest = String.join("\r\n",
                "POST /hi HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "");
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when
        HttpRequest request = HttpRequestReader.readHttpRequest(reader);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(request.getPath()).isEqualTo("/hi"),
                () -> assertThat(request.getVersion()).isEqualTo(HttpVersion.HTTP_1_1),
                () -> assertThat(request.getHeaders()).hasSize(2),
                () -> assertThat(request.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded"),
                () -> assertThat(request.getBody()).isNull()

        );
    }

    @Test
    @DisplayName("잘못된 형식의 요청 읽기 테스트")
    void readMalformedRequest() {
        // given
        String rawRequest = "INVALID REQUEST";
        BufferedReader reader = new BufferedReader(new StringReader(rawRequest));

        // when & then
        assertThatThrownBy(() -> HttpRequestReader.readHttpRequest(reader))
                .isInstanceOf(ArrayIndexOutOfBoundsException.class);
    }
}
