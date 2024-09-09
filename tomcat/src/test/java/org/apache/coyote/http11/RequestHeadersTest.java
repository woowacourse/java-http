package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RequestHeadersTest {

    @DisplayName("Request Header를 파싱하는데 성공한다.")
    @Test
    void parseRequestHeader() {
        // given
        List<String> rawRequestHeaders = List.of(
                "Cookie: JSESSIONID=f123234234;",
                "Content-Length: 53 ",
                "Accept: someValue",
                "Host: localhost"
        );
        // when
        RequestHeaders requestHeaders = new RequestHeaders(rawRequestHeaders);
        // then
        assertThat(requestHeaders.getHeaderValue("Cookie")).isEqualTo("JSESSIONID=f123234234;");
    }

    @DisplayName("Request 값이 없을 경우 빈 값을 반환한다.")
    @Test
    void getEmptyHeaderValue() {
        // given
        List<String> rawRequestHeaders = List.of();
        // when
        RequestHeaders requestHeaders = new RequestHeaders(rawRequestHeaders);
        // then
        assertThat(requestHeaders.getHeaderValue("Cookie")).isEqualTo("");
    }
}
