package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("요청에 쿼리가 포함하면 true를 반환한다.")
    void hasQuery_true() {
        final HttpRequest httpRequest = new HttpRequest(
                "GET /login?account=gugu&password=password HTTP/1.1",
                new Header(new HashMap<>()),
                ""
        );

        assertThat(httpRequest.hasQuery()).isTrue();
    }

    @Test
    @DisplayName("요청에 쿼리가 포함되어있지않으면 false를 반환한다.")
    void hasQuery_false() {
        final HttpRequest httpRequest = new HttpRequest(
                "GET /login",
                new Header(new HashMap<>()),
                ""
        );

        assertThat(httpRequest.hasQuery()).isFalse();
    }

    @Test
    @DisplayName("쿼리내의 요소를 반환한다.")
    void getQueryByValue() {
        final HttpRequest httpRequest = new HttpRequest(
                "GET /login?account=gugu&password=password HTTP/1.1",
                new Header(new HashMap<>()),
                "");

        assertThat(httpRequest.getQueryByValue("account")).isEqualTo("gugu");
        assertThat(httpRequest.getQueryByValue("password")).isEqualTo("password");
    }

    @Test
    @DisplayName("쿼리내의 요소가 없다면 예외를 반환한다.")
    void getQueryByValue_NotFound() {
        final HttpRequest httpRequest = new HttpRequest(
                "GET /login?account=gugu&password=password HTTP/1.1",
                new Header(new HashMap<>()),
                ""
        );

        assertThatThrownBy(() -> httpRequest.getQueryByValue("test"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("test의 값을 찾을 수 없습니다.");
    }
}
