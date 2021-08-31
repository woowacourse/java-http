package nextstep.jwp.http.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResponseHeaders 테스트")
class ResponseHeadersTest {

    @DisplayName("쿠키와 쿠키 아닌 헤더 분리 테스트 - 쿠키가 없을 때")
    @Test
    void classifyHeadersWhenCookieNotExists() {
        //given
        final ResponseHeaders responseHeaders = new ResponseHeaders();

        //when
        responseHeaders.addExceptCookie("Host", "localhost:8080");
        responseHeaders.addExceptCookie("Connection", "keep-alive");
        final String responseHeadersString = responseHeaders.toString();

        //then
        assertThat(responseHeadersString)
                .contains("Host: localhost:8080 ")
                .contains("Connection: keep-alive ");
    }

    @DisplayName("쿠키와 쿠키 아닌 헤더 분리 테스트 - 쿠키가 1개 존재할 때")
    @Test
    void classifyHeadersWhenOneCookieExists() {
        //given
        final ResponseHeaders responseHeaders = new ResponseHeaders();

        //when
        responseHeaders.addExceptCookie("Host", "localhost:8080");
        responseHeaders.addExceptCookie("Connection", "keep-alive");
        responseHeaders.addCookie("yummy_cookie", "choco");
        final String responseHeadersString = responseHeaders.toString();

        //then
        assertThat(responseHeadersString)
                .contains("Host: localhost:8080 ")
                .contains("Connection: keep-alive ")
                .contains("Set-Cookie: yummy_cookie=choco ");
    }

    @DisplayName("쿠키와 쿠키 아닌 헤더 분리 테스트 - 쿠키가 2개 존재할 때")
    @Test
    void classifyHeadersWhenTwoCookiesExist() {
        //given
        final ResponseHeaders responseHeaders = new ResponseHeaders();

        //when
        responseHeaders.addExceptCookie("Host", "localhost:8080");
        responseHeaders.addExceptCookie("Connection", "keep-alive");
        responseHeaders.addCookie("yummy_cookie", "choco");
        responseHeaders.addCookie("tasty_cookie", "strawberry");
        final String responseHeadersString = responseHeaders.toString();

        //then
        assertThat(responseHeadersString)
                .contains("Host: localhost:8080 ")
                .contains("Connection: keep-alive ")
                .contains("Set-Cookie: yummy_cookie=choco ")
                .contains("Set-Cookie: tasty_cookie=strawberry ");
    }
}