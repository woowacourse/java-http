package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RequestTest {

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
            Request request = new Request(new ByteArrayInputStream(input.getBytes()));

            //then
            assertAll(
                    () -> assertThat(request.getMethod()).isEqualTo(Method.GET),
                    () -> assertThat(request.getTarget()).isEqualTo("/index.html")
            );
        }
    }
}
