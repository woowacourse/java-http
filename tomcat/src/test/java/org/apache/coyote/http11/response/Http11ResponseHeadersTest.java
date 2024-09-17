package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class Http11ResponseHeadersTest {

    @Test
    @DisplayName("빌더를 통해 생성할 수 있다.")
    void builder() {
        assertThatCode(() ->
                Http11ResponseHeaders.builder()
                        .addHeader("key", "value")
                        .build())
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("빈 헤더를 생성할 수 있다.")
    void instance() {
        Http11ResponseHeaders headers = Http11ResponseHeaders.instance();

        assertThat(headers.asString()).isEmpty();
    }

    @Test
    @DisplayName("헤더를 String 형태로 변환할 수 있다.")
    void asString() {
        Http11ResponseHeaders headers = Http11ResponseHeaders.builder()
                .addHeader("key1", "value1")
                .addHeader("key2", "value2")
                .build();

        assertThat(headers.asString()).isEqualTo(
                "key1: value1 " + "\r\n" +
                        "key2: value2 " + "\r\n"
        );
    }

    @Test
    @DisplayName("Location 헤더를 추가할 수 있다.")
    void addLocation() {
        Http11ResponseHeaders headers = Http11ResponseHeaders.instance();
        headers.addLocation("/index.html");

        assertThat(headers.asString()).isEqualTo(
                "Location: /index.html " + "\r\n"
        );
    }

    @Test
    @DisplayName("Set-Cookie 헤더를 추가할 수 있다.")
    void addSetCookie() {
        Http11ResponseHeaders headers = Http11ResponseHeaders.instance();
        headers.addSetCookie("JSESSIONID", "123-qdj92-12eh2eh");

        assertThat(headers.asString()).isEqualTo(
                "Set-Cookie: JSESSIONID=123-qdj92-12eh2eh " + "\r\n"
        );
    }
}
