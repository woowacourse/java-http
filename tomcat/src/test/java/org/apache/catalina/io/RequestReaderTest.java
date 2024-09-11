package org.apache.catalina.io;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class RequestReaderTest {

    @Nested
    @DisplayName("request 읽기")
    class readRequest {
        @Test
        @DisplayName("성공 : 문자열이 존재한다면 성공")
        void readRequestSuccess() {
            final String httpRequest = String.join("\r\n",
                    "GET / HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "");

            final var socket = new StubSocket(httpRequest);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            List<String> requestHeader = RequestReader.readRequest(reader);

            assertThat(requestHeader).containsExactly(
                    "GET / HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ");
        }

        @Test
        @DisplayName("실패 : 문자열이 존재하지 않으면 예외 처리")
        void readRequestFailEmptyString() {
            final String httpRequest = "";

            final var socket = new StubSocket(httpRequest);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            assertThatThrownBy(() -> RequestReader.readRequest(reader))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("요청 헤더가 비어 있습니다.");
        }
    }

    @Nested
    @DisplayName("body 읽기")
    class readBody {
        @Test
        @DisplayName("성공 : 문자열과 길이가 동일하다면 성공")
        void readBodySuccess() {
            final String body = "account=kyum&password=password&email=kyum@naver.com";
            final String httpRequest = String.join("\r\n", body);
            final var socket = new StubSocket(httpRequest);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String requestBody = RequestReader.readBody(reader, body.getBytes().length);

            assertThat(requestBody).isEqualTo(body);
        }

        @Test
        @DisplayName("실패 : 문자열과 길이가 다르다면 예외 처리")
        void readBodyFailBodyNotMatchLength() {
            final String body = "account=kyum&password=password&email=kyum@naver.com";
            final String httpRequest = String.join("\r\n", body);
            final var socket = new StubSocket(httpRequest);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            assertThatCode(() -> RequestReader.readBody(reader, body.getBytes().length + 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("실제 읽은 바이트 수가 예상된 길이보다 작습니다.");
        }
    }
}
