package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("요청 헤더에서 Cookie 키-값을 모두 반환한다.")
    @Test
    void getCookieString() {
        List<String> headers = List.of(
                "Connection: keep-alive",
                "Content-Length: 80",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        assertThat(requestHeaders.getCookieString())
                .isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("요청 헤더에서 Content-Length 값을 반환한다.")
    @Test
    void getContentLength() {
        List<String> headers = List.of(
                "Connection: keep-alive",
                "Content-Length: 80",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );
        RequestHeaders requestHeaders = new RequestHeaders(headers);

        assertThat(requestHeaders.getContentLength()).isEqualTo(80);
    }
}
