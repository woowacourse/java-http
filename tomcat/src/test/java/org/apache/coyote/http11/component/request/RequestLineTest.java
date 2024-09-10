package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.net.URI;

import org.apache.coyote.http11.component.common.Method;
import org.apache.coyote.http11.component.common.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("요청 라인을 생성한다.")
    void generate_request_line() {
        // given
        final var plaintext = "GET /index.html HTTP/1.1";

        // when & then
        assertThatCode(() -> new RequestLine(plaintext))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("올바르지 않는 요청 라인 생성 시 예외를 발생한다.")
    void throw_exception_when_generate_invalid_request_line() {
        // given
        final var plaintext = "GET /index.html HTTP/1.1 Cheese";

        // when & then
        assertThatThrownBy(() -> new RequestLine(plaintext))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("URI에서 Query의 키로 값을 찾는다.")
    void search_query_value_via_query_key() {
        // given
        final var requestLine = new RequestLine(Method.CONNECT,
                URI.create("/example?name=fram&password=secret"), new Version(1, 1));

        // when
        final var value = requestLine.getQueryValue("name");

        // then
        assertThat(value).isEqualTo("fram");
    }

    @Test
    @DisplayName("없는 키로 Query값을 찾으면 예외를 발생시킨다.")
    void throw_exception_when_search_does_not_exist_query() {
        // given
        final var requestLine = new RequestLine(Method.CONNECT,
                URI.create("/example?name=fram&password=secret"), new Version(1, 1));

        // when & then
        assertThatThrownBy(() -> requestLine.getQueryValue("weight"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
