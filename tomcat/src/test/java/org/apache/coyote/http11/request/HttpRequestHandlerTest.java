package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.Socket;
import org.apache.coyote.http11.request.model.HttpMethod;
import org.apache.coyote.http11.request.model.HttpRequest;
import org.apache.coyote.http11.request.model.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class HttpRequestHandlerTest {

    @Nested
    @DisplayName("http 요청을 받아 httpRequest 객체를 생성한다.")
    class CreateHttpRequest {

        @Test
        @DisplayName("요청의 첫 줄을 method, uri, version으로 분리한다.")
        void firstLine() {
            String request = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            Socket socket = new StubSocket(request);

            HttpRequest httpRequest = HttpRequestHandler.newHttpRequest(socket);

            assertAll(
                    () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                    () -> assertThat(httpRequest.getUri().getValue()).isEqualTo("/index.html"),
                    () -> assertThat(httpRequest.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
            );
        }

        @Test
        @DisplayName("요청의 헤더를 분리한다.")
        void headers() {
            String request = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "dasd");

            Socket socket = new StubSocket(request);

            HttpRequest httpRequest = HttpRequestHandler.newHttpRequest(socket);
            HttpHeaders headers = httpRequest.getHeaders();

            assertAll(
                    () -> assertThat(headers.getValue("Host")).isEqualTo("localhost:8080"),
                    () -> assertThat(headers.getValue("Connection")).isEqualTo("keep-alive")
            );
        }
    }

}
