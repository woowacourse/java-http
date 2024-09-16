package org.apache.coyote.http11.message.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http11.message.common.HttpHeader;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersParserTest {

    @DisplayName("여러개의 헤더 텍스트를 파싱할 수 있다.")
    @Test
    void parseSuccess() {
        List<String> headers = List.of(
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        HttpHeaders httpHeaders = HttpHeadersParser.parse(headers);

        assertAll(
                () -> assertThat(httpHeaders.getValue(HttpHeader.HOST)).isEqualTo("localhost:8080"),
                () -> assertThat(httpHeaders.getValue(HttpHeader.COOKIE)).isEqualTo(
                        "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @DisplayName("key:value 형식의 헤더가 아니면 예외가 발생한다.")
    @Test
    void parseFailure() {
        List<String> headers = List.of(
                "Host=localhost:8080",
                "Cookie= yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        assertThatThrownBy(() -> HttpHeadersParser.parse(headers))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
