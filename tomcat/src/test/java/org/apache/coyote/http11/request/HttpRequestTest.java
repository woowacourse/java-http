package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void 요청을_읽고_생성한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        // when
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        // then
        assertThat(request).isEqualTo(new HttpRequest
                (
                        RequestLine.from("GET /index.html HTTP/1.1 "),
                        RequestHeader.from(String.join(System.lineSeparator(),
                                "Host: localhost:8080 ",
                                "Connection: keep-alive "))
                )
        );
    }

    @Nested
    class 요청에_body가_있는지_확인한다 {
        @Test
        void header에_Content_Length가_없을_때() {
            // given
            String requestLine = "GET /index.html HTTP/1.1 ";
            String requestHeader = String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ");
            HttpRequest request = HttpRequest.of(requestLine, requestHeader);

            // when
            boolean actual = request.hasBody();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void header에_Content_Length가_0일_때() {
            // given
            String requestLine = "POST /login HTTP/1.1 ";
            String requestHeader = String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 0 ");
            HttpRequest request = HttpRequest.of(requestLine, requestHeader);

            // when
            boolean actual = request.hasBody();

            // then
            assertThat(actual).isFalse();
        }

        @Test
        void header에_Content_Length가_양수일_때() {
            // given
            String requestLine = "POST /login HTTP/1.1 ";
            String requestHeader = String.join(System.lineSeparator(),
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 10 ");
            HttpRequest request = HttpRequest.of(requestLine, requestHeader);

            // when
            boolean actual = request.hasBody();

            // then
            assertThat(actual).isTrue();
        }
    }

    @Test
    void Content_Length를_반환한다() {
        // given
        String requestLine = "POST /login HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ");
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        // when
        int contentLength = request.contentLength();

        // then
        assertThat(contentLength).isEqualTo(10);
    }

    @Test
    void Request_Line의_구성을_확인한다() {
        // given
        String requestLine = "POST /login HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ");
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        // when
        boolean actual = request.consistsOf(POST, "/login", "/login.html");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Request_Line에_Query_String_이_있는지_확인한다() {
        // given
        String requestLine = "GET /login?account=gugu&password=password HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ");
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        // when
        boolean actual = request.hasQueryString();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Body의_값을_조회한다() {
        String requestLine = "POST /login HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ");
        String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);
        request.setRequestBody(requestBody);

        // when
        String actual = request.getBodyValue("account");

        // then
        assertThat(actual).isEqualTo("gugu");
    }

    @Test
    void Session_Id가_있는지_확인한다() {
        // given
        String requestLine = "POST /login HTTP/1.1 ";
        String requestHeader = String.join(System.lineSeparator(),
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 10 ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ");
        HttpRequest request = HttpRequest.of(requestLine, requestHeader);

        // when
        boolean actual = request.hasSessionId();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Session_Id를_얻는다() {
        // given

        // when

        // then

    }
}
