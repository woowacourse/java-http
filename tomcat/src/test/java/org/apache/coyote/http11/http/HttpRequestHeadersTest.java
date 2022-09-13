package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestHeadersTest {

    @Test
    void 요청값을_파싱해서_Map_형식으로_만든다() {
        // given
        final List<String> httpHeaders = List.of("content-length: 999 ",
                "Connection: keep-alive ",
                "Cookie: tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        // when
        final HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(httpHeaders);

        // then
        assertAll(
                () -> assertThat(httpRequestHeaders.getContentLength())
                        .usingRecursiveComparison()
                        .isEqualTo(ContentLength.from("999")),
                () -> assertThat(httpRequestHeaders.getCookies().getJSessionId())
                        .usingRecursiveComparison()
                        .isEqualTo(Cookies.from(
                                        "Cookie: tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46")
                                .getJSessionId())
        );
    }
}
