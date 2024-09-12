package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.exception.IllegalHttpMessageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RequestLineTest {

    @Nested
    @DisplayName("파싱 테스트")
    class ParseFromTest {
        @Test
        @DisplayName("파싱 성공")
        void success() {
            // given
            String requestLineText = "GET /index.html HTTP/1.1 ";

            // when
            RequestLine requestLine = RequestLine.parseFrom(requestLineText);

            //then
            assertAll(
                    () -> assertThat(requestLine.httpVersion()).isEqualTo("HTTP/1.1"),
                    () -> assertThat(requestLine.method()).isEqualTo(Method.GET),
                    () -> assertThat(requestLine.target()).isEqualTo("/index.html")
            );
        }

        @Test
        @DisplayName("잘못된 형식 파싱 실패")
        void IllegalFormTest() {
            // given
            String wrongRequestLine = "GET /index.html";

            // when & then
            assertThatThrownBy(() -> RequestLine.parseFrom(wrongRequestLine))
                    .isInstanceOf(IllegalHttpMessageException.class)
                    .hasMessageContaining("잘못된 헤더 형식입니다.");
        }
    }

    @Nested
    @DisplayName("정적 리소스 확인 테스트")
    class IsStaticResourceRequestTest {

        @ParameterizedTest(name = "{0} 확장자 파일을 요청하면 정적 리소스로 판단한다.")
        @ValueSource(strings = {".css", ".html", ".js"})
        void staticResourceTest(String extension) {
            // given
            String target = "/somePath" + extension;
            String requestLineText = "GET " + target + " HTTP/1.1";

            //when
            RequestLine requestLine = RequestLine.parseFrom(requestLineText);

            //then
            assertThat(requestLine.isStaticResourceRequest()).isTrue();
        }

        @Test
        @DisplayName("파일 확장자가 없으면 정적 리소스로 판단하지 않는다.")
        void noExtensionPathTest() {
            // given
            String target = "/somePath";
            String requestLineText = "GET " + target + " HTTP/1.1";

            //when
            RequestLine requestLine = RequestLine.parseFrom(requestLineText);

            //then
            assertThat(requestLine.isStaticResourceRequest()).isFalse();
        }

        @Test
        @DisplayName("css, js, html 이외의 파일 확장자가 있으면 정적 리소스로 판단하지 않는다.")
        void otherExtensionPathTest() {
            // given
            String target = "/somePath.java";
            String requestLineText = "GET " + target + " HTTP/1.1";

            //when
            RequestLine requestLine = RequestLine.parseFrom(requestLineText);

            //then
            assertThat(requestLine.isStaticResourceRequest()).isFalse();
        }
    }
}
