package org.apache.coyote.response;

import static common.ResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void 응답_본문을_반환한다() {
        // given
        HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
        httpResponse.setResponseMessage(OK, "Hello world!");

        // when
        String message = httpResponse.responseMessage();

        // then
        assertThat(message).isEqualTo(
                String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 12 ",
                        "",
                        "Hello world!"
                )
        );
    }

    @Nested
    class Http_Response가_결정되었는지_확인한다 {

        @Test
        void 결정되지_않았을_때() {
            // given
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1");

            // when
            boolean actual = httpResponse.isDetermined();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void 결정되었을_때() {
            // given
            HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
            httpResponse.setResponseMessage(OK, "Hello world!");

            // when
            boolean actual = httpResponse.isDetermined();

            // then
            assertThat(actual).isTrue();
        }
    }
}
