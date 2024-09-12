package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Nested
    @DisplayName("생성 테스트")
    class ConstructorTest {

        @Test
        @DisplayName("GET 요청 생성 테스트")
        void getRequestConstructTest() throws IOException {
            //given
            String input = """
                    GET /index.html HTTP/1.1
                    Host: localhost:8080
                    Connection: keep-alive
                    
                    """;

            //when
            InputStream requestStream = new ByteArrayInputStream(input.getBytes());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(requestStream));


            HttpRequest httpRequest = HttpRequest.readFrom(bufferedReader);

            //then
            assertAll(
                    () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                    () -> assertThat(httpRequest.getTarget()).isEqualTo("/index.html")
            );
        }
    }
}
