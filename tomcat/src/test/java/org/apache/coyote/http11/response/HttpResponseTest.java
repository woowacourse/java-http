package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Nested
    class 객체_생성_테스트 {

        @Test
        void GET_메서드에_대한_HttpResponse_객체를_생성한다() {
            // given
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.OK);
            final var responseHeader = ResponseHeader.createEmpty();
            responseHeader.addHeader("Content-Type", "text/html");
            responseHeader.addHeader("Content-Length", "12");
            final var responseBody = "Hello World!";

            // when
            final var actual = HttpResponse.createGetResponse(statusLine, responseHeader, responseBody);

            // then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.getStatusLine())
                        .usingRecursiveComparison()
                        .isEqualTo(statusLine);
                softAssertions.assertThat(actual.getResponseHeader())
                        .usingRecursiveComparison()
                        .isEqualTo(responseHeader);
                softAssertions.assertThat(actual.getResponseBody())
                        .isEqualTo(responseBody);
            });
        }

        @Test
        void POST_메서드에_대한_HttpResponse_객체를_생성한다() {
            // given
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.OK);
            final var responseHeader = ResponseHeader.createEmpty();
            responseHeader.addHeader("Location", "/index.html");

            // when
            final var actual = HttpResponse.createPostResponse(statusLine, responseHeader);

            // then
            assertSoftly(softAssertions -> {
                softAssertions.assertThat(actual.getStatusLine())
                        .usingRecursiveComparison()
                        .isEqualTo(statusLine);
                softAssertions.assertThat(actual.getResponseHeader())
                        .usingRecursiveComparison()
                        .isEqualTo(responseHeader);
            });
        }
    }
}
