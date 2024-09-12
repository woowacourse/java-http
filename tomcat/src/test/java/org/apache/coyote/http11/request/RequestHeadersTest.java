package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("객체 생성 시 문자열을 헤더의 형태로 입력받아 필드에 저장한다.")
    @Test
    void should_readHeaders_when_construct() throws IOException {
        // given
        String input = "Host: localhost:8080\r\n"
                + "Connection: keep-alive\r\n"
                + "Accept: */*\r\n"
                + "\r\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));

        // when
        RequestHeaders requestHeaders = new RequestHeaders(reader);

        // then
        assertThat(requestHeaders.get("Host").get().getValue()).isEqualTo("localhost:8080");
        assertThat(requestHeaders.get("Connection").get().getValue()).isEqualTo("keep-alive");
        assertThat(requestHeaders.get("Accept").get().getValue()).isEqualTo("*/*");
    }

    @DisplayName("객체 생성 시 헤더의 형태가 아닌 문자열이 입력된 경우 예외가 발생한다.")
    @Test
    void should_throwException_when_readInvalidHeadersInConstructor() {
        // given
        String input = "invalid header";
        BufferedReader reader = new BufferedReader(new StringReader(input));

        // when & then
        assertThatThrownBy(() -> new RequestHeaders(reader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 형태의 헤더입니다.");
    }
}
