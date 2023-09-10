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

    @Nested
    class 객체_생성_테스트 {

        @Test
        void GET_메서드에_대한_HttpResponse_객체를_생성한다() {
            // given
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.OK);
            final var responseHeader = ResponseHeader.createEmpty();
            responseHeader.addHeader("Content-Type", "text/html");
            responseHeader.addHeader("Content-Length", "12");
            final var responseBody = ResponseBody.fromText("Hello World!");

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
                        .usingRecursiveComparison()
                        .isEqualTo(responseBody);
            });
        }

        @Test
        void POST_메서드에_대한_HttpResponse_객체를_생성한다() {
            // given
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.FOUND);
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

    @Nested
    class toString_테스트 {

        @Test
        void ResponseBody가_없는_경우의_HttpResponse_메세지를_출력한다() {
            // given
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.FOUND);
            final var responseHeader = ResponseHeader.createEmpty();
            responseHeader.addHeader("Location", "/index.html");
            final var httpResponse = HttpResponse.createPostResponse(statusLine, responseHeader);

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
            final var statusLine = StatusLine.of("HTTP/1.1", HttpStatus.OK);
            final var responseHeader = ResponseHeader.createEmpty();
            responseHeader.addHeader("Content-Type", "text/html");
            responseHeader.addHeader("Content-Length", "12");
            final var responseBody = ResponseBody.fromText("Hello World!");
            final var httpResponse = HttpResponse.createGetResponse(statusLine, responseHeader, responseBody);

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
