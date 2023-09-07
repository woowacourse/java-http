package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestHeaderTest {

    @Test
    void requestHeader를_입력해_생성한다() {
        // given
        String requestHeader = String.join(
                System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );

        // when
        RequestHeader header = RequestHeader.from(requestHeader);

        // then
        assertThat(header).isEqualTo(new RequestHeader(
                Map.of(
                        "Host", "localhost:8080",
                        "Connection", "keep-alive"
                )
        ));
    }

    @Test
    void header_field_이름을_입력하면_value를_반환한다() {
        // given
        String requestHeader = String.join(
                System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        RequestHeader header = RequestHeader.from(requestHeader);

        // when
        String value = header.get("Host");

        // then
        assertThat(value).isEqualTo("localhost:8080");
    }

    @Nested
    class header로_요청에_Content가_있는지_확인한다 {
        @Test
        void header에_Content_Length가_없을_때() {
            // given
            String requestHeader = String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ");
            RequestHeader header = RequestHeader.from(requestHeader);

            // when
            boolean actual = header.hasContent();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void header에_Content_Length가_0일_때() {
            // given
            String requestHeader = String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 0 ");
            RequestHeader header = RequestHeader.from(requestHeader);

            // when
            boolean actual = header.hasContent();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void header에_Content_Length가_있을_때() {
            // given
            String requestHeader = String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 10 ");
            RequestHeader header = RequestHeader.from(requestHeader);

            // when
            boolean actual = header.hasContent();

            // then
            assertThat(actual).isTrue();
        }
    }

    @Test
    void Content_Length를_반환한다() {
        // given
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ");
        RequestHeader header = RequestHeader.from(requestHeader);

        // when
        int contentLength = header.contentLength();

        // then
        assertThat(contentLength).isEqualTo(10);
    }

    @Test
    void Session_Id을_포함하는지_확인한다() {
        // given
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 "
        );
        RequestHeader header = RequestHeader.from(requestHeader);

        // when
        boolean actual = header.hasSessionId();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Session_Id를_얻는다() {
        // given
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 "
        );
        RequestHeader header = RequestHeader.from(requestHeader);

        // when
        String sessionId = header.sessionId();

        // then
        assertThat(sessionId).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    void Session_Id를_얻을_때_Session_Id가_없는_경우_예외를_던진다() {
        // given
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ");
        RequestHeader header = RequestHeader.from(requestHeader);

        // expect
        assertThatThrownBy(header::sessionId)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("세션 Id가 존재하지 않습니다.");
    }
}
