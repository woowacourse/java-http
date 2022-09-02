package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.LoginFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("생성시 uri 쿼리 스트링에 로그인 정보가 db에 있으면 예외가 발생하지 않는다.")
    @Test
    void 생성자_테스트_유저_정보_일치() throws IOException {
        // given
        String requestUri = "/login?account=gugu&password=password";
        Map<String, String> requestHeadersMap = Map.of("Host", "localhost:8080",
                "Connection", "keep-alive");

        // when, then
        assertDoesNotThrow(() -> new RequestHeaders(requestHeadersMap, requestUri));
    }

    @DisplayName("생성시 uri 쿼리 스트링에 로그인 정보가 db에 없으면 예외가 발생한다.")
    @Test
    void 생성자_테스트_유저_정보_불일치() throws IOException {
        // given
        String requestUri = "/login?account=gugu&password=가짜password";
        Map<String, String> requestHeadersMap = Map.of("Host", "localhost:8080",
                "Connection", "keep-alive");

        // when, then
        assertThatThrownBy(() -> new RequestHeaders(requestHeadersMap, requestUri))
                .isInstanceOf(LoginFailureException.class);
    }

    @DisplayName("header에 accept 필드가 없으면 에러를 발생시킨다.")
    @Test
    void getAcceptHeaderValue() {
        //given
        String requestUri = "/login?account=gugu&password=password";
        Map<String, String> requestHeadersMap = Map.of("Host", "localhost:8080",
                "Connection", "keep-alive");
        RequestHeaders requestHeaders = new RequestHeaders(requestHeadersMap, requestUri);

        //when, then
        assertThatThrownBy(requestHeaders::getAcceptHeaderValue)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
