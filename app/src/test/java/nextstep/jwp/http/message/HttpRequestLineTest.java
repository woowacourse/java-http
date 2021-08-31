package nextstep.jwp.http.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpRequestLineTest {

    @DisplayName("문자열 형식으로 HttpRequestLine 객체를 만들 수 있다.")
    @Test
    void createByString_변환_성공() {
        // given
        String requestLine = "GET /index.html HTTP/1.1";

        // when
        HttpRequestLine actual = HttpRequestLine.createByString(requestLine);

        // then
        assertThat(actual.asString()).isEqualTo(requestLine);
        assertThat(actual.getMethod()).isSameAs(HttpMethod.GET);
        assertThat(actual.getUri()).get().isEqualTo("/index.html");
        assertThat(actual.getVersionOfProtocol()).get().isEqualTo("HTTP/1.1");
    }

    @DisplayName("문자열 형식 HttpRequestLine이 스펙을 지키지 않을 시 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"GET /index", "GET HTTP/1.1", "GGET /index HTTP/1.1"})
    void createByString_변환_실패(String requestLine) {
        // when, then
        assertThatThrownBy(() -> HttpRequestLine.createByString(requestLine))
            .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("URI Path에 QueryString이 존재하면 Optional을 감싼 문자열을 반환한다.")
    @Test
    void getQueryString_존재_할_경우_반환() {
        // given
        String queryString = "account=binghe&password=pw";
        String requestLine = "GET /index?" + queryString + " HTTP/1.1";

        // when
        HttpRequestLine actual = HttpRequestLine.createByString(requestLine);

        // then
        assertThat(actual.getQueryString()).get().isEqualTo(queryString);
    }

    @DisplayName("URI Path에 QueryString이 존재하지 않을 경우 Optional.empty를 반환한다.")
    @Test
    void getQueryString_존재_하지_않을_경우_empty() {
        // given
        String requestLine = "GET /index HTTP/1.1";

        // when
        HttpRequestLine actual = HttpRequestLine.createByString(requestLine);

        // then
        assertThat(actual.getQueryString()).isSameAs(Optional.empty());
    }
}
