package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("header에 accept 필드가 없으면 에러를 발생시킨다.")
    @Test
    void getAcceptHeaderValue() {
        //given
        String requestUri = "/login?account=gugu&password=password";
        Map<String, String> requestHeadersMap = Map.of("Host", "localhost:8080",
                "Connection", "keep-alive");
        RequestHeaders requestHeaders = new RequestHeaders(requestHeadersMap);

        //when, then
        assertThatThrownBy(requestHeaders::getAcceptHeaderValue)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
