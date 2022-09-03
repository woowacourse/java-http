package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.IOException;
import java.util.Map;
import org.apache.coyote.http11.LoginFailureException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("생성시 uri 쿼리 스트링에 로그인 정보가 db에 있으면 예외가 발생하지 않는다.")
    @Test
    void 생성자_테스트_유저_정보_일치() throws IOException {
        // given
        String requestLine = "GET //login?account=gugu&password=password HTTP/1.1 ";
        Map<String, String> requestHeadersMap = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive");

        // when, then
        assertDoesNotThrow(() -> new RequestLine(requestLine));
    }

    @DisplayName("생성시 uri 쿼리 스트링에 로그인 정보가 db에 없으면 예외가 발생한다.")
    @Test
    void 생성자_테스트_유저_정보_불일치() throws IOException {
        // given
        String requestLine = "GET //login?account=gugu&password=가짜password HTTP/1.1 ";
        Map<String, String> requestHeadersMap = Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive");

        // when, then
        assertThatThrownBy(() -> new RequestLine(requestLine))
                .isInstanceOf(LoginFailureException.class);
    }

}
