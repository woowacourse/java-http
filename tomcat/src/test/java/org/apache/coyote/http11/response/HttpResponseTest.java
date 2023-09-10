package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void 비어있는_HttpResponse_객체를_생성한다() {
        // given & when
        final var actual = HttpResponse.createEmpty();

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getStatusLine())
                    .isNull();
            softAssertions.assertThat(actual.getResponseHeader().getHeaders())
                    .isEmpty();
            softAssertions.assertThat(actual.getResponseBody())
                    .isNull();
        });
    }

    @Nested
    class toString_테스트 {

        @Test
        void ResponseBody가_없는_경우의_HttpResponse_메세지를_출력한다() {
            // given
            final var httpResponse = HttpResponse.createEmpty();
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.FOUND);
            httpResponse.setStatusLine(statusLine);
            httpResponse.addResponseHeader("Location", "/index.html");

            final var expected = String.join("\r\n",
                    "HTTP/1.1 302 Found",
                    "Location: /index.html");

            // when
            final var actual = httpResponse.toString();

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void ResponseBody가_있는_경우의_HttpResponse_메세지를_출력한다() {
            // given
            final var httpResponse = HttpResponse.createEmpty();
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.OK);
            httpResponse.setStatusLine(statusLine);
            httpResponse.addResponseHeader("Content-Type", "text/html");
            httpResponse.addResponseHeader("Content-Length", "12");
            final var responseBody = ResponseBody.fromText("Hello World!");
            httpResponse.setResponseBody(responseBody);

            final var expected = String.join("\r\n",
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/html",
                    "Content-Length: 12",
                    "",
                    "Hello World!");

            // when
            final var actual = httpResponse.toString();

            // then
            assertThat(actual).isEqualTo(expected);
        }
    }
}
