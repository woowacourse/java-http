package org.apache.coyote.http11.response;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.response.HttpStatus.FOUND;
import static org.apache.coyote.http11.response.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseGeneratorTest {

    private final HttpResponseGenerator responseGenerator = new HttpResponseGenerator();

    @Nested
    class ResponseEntity를_생성한다 {

        @Test
        @DisplayName("URI가 /(슬래쉬)라면 body에 Hello world를 담아서 반환한다.")
        void generateDefaultResponseEntity() throws IOException {
            // given
            final ResponseEntity responseEntity = new ResponseEntity(OK, "/");

            // when
            String response = responseGenerator.generate(responseEntity);

            // then
            assertThat(response).contains("Hello world!");
        }

        @DisplayName("HTTP STATUS가 FOUND라면 index.html 리다이렉트 Response Entity를 반환한다.")
        @Test
        void generateFoundResponseEntity() throws IOException {
            // given
            ResponseEntity responseEntity = new ResponseEntity(FOUND, "index.html");

            // when
            String response = responseGenerator.generate(responseEntity);

            // then
            assertThat(response).contains("Location: index.html");
        }

        @DisplayName("URI가 /(슬래쉬)가 아니고 HTTP STATUS가 FOUND가 아니라면 커스텀 정적 파일을 반환한다.")
        @Test
        void generateCustomResponseEntity() throws IOException {
            // given
            ResponseEntity responseEntity = new ResponseEntity(FOUND, "index.html");

            // when
            String response = responseGenerator.generate(responseEntity);

            // then
            assertThat(response).contains("Location: index.html");
        }

    }
}
