package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpVersion;
import org.apache.coyote.util.HttpRequestUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

public class HttpRequestUtilsTest {

    @Nested
    @DisplayName("http 요청을 받아 httpRequest 객체를 생성한다.")
    class CreateHttpRequest {

        @Test
        @DisplayName("요청의 첫 줄을 method, uri, version으로 분리한다.")
        void firstLine() throws IOException {
            String request = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            Socket socket = new StubSocket(request);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            HttpRequest httpRequest = HttpRequestUtils.newHttpRequest(bufferedReader);

            assertAll(
                    () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                    () -> assertThat(httpRequest.getPath().getValue()).isEqualTo("/index.html"),
                    () -> assertThat(httpRequest.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
            );
        }

        @Test
        @DisplayName("요청의 헤더를 분리한다.")
        void headers() throws IOException {
            String request = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "dasd");

            Socket socket = new StubSocket(request);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            HttpRequest httpRequest = HttpRequestUtils.newHttpRequest(bufferedReader);
            HttpHeaders headers = httpRequest.getHeaders();

            assertAll(
                    () -> assertThat(headers.getValue("Host")).isEqualTo("localhost:8080"),
                    () -> assertThat(headers.getValue("Connection")).isEqualTo("keep-alive")
            );
        }

        @Test
        @DisplayName("요청의 요청 본문을 가져온다.")
        void body() throws IOException {
            String requestBody = "{\n"
                    + "\"account\" : \"gugu\",\n"
                    + "\"password\" : \"password\"\n"
                    + "}";
            String request = "POST /index.html HTTP/1.1\n"
                    + "Host: localhost:8080\n"
                    + "Content-Length: " + requestBody.getBytes().length + "\n"
                    + "Connection: keep-alive\n"
                    + "Accept: */*\n"
                    + "\n"
                    + requestBody;

            Socket socket = new StubSocket(request);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            HttpRequest httpRequest = HttpRequestUtils.newHttpRequest(bufferedReader);
            String body = httpRequest.getBody();

            assertThat(body).isEqualTo(requestBody);
        }
    }
}
