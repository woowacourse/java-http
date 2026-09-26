package org.apache.coyote.http11;

import org.apache.coyote.exception.HttpParseException;
import org.apache.coyote.http11.request.RequestUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("요청 URI 해석")
class RequestUriTest {

    @Test
    @DisplayName("쿼리스트링이 없으면 경로만 가진다")
    void pathWithoutQueryString() {
        // when
        final RequestUri uri = new RequestUri("/login");

        // then
        assertThat(uri.getPath()).isEqualTo("/login");
        assertThat(uri.findParameter("account")).isEmpty();
    }

    @Test
    @DisplayName("경로와 쿼리스트링을 분리한다")
    void separatePathAndQueryString() {
        // when
        final RequestUri uri = new RequestUri("/login?account=gugu&password=secret");

        // then
        assertThat(uri.getPath()).isEqualTo("/login");
        assertThat(uri.findParameter("account")).contains("gugu");
        assertThat(uri.findParameter("password")).contains("secret");
    }

    @Test
    @DisplayName("퍼센트 인코딩된 값을 디코딩한다")
    void decodePercentEncodedValue() {
        // when
        final RequestUri uri = new RequestUri("/register?email=hkkang%40woowahan.com");

        // then
        assertThat(uri.findParameter("email")).contains("hkkang@woowahan.com");
    }

    @Test
    @DisplayName("쿼리스트링 안의 물음표는 값으로 유지한다")
    void keepQuestionMarkInsideQueryString() {
        // when
        final RequestUri uri = new RequestUri("/search?query=what?");

        // then
        assertThat(uri.getPath()).isEqualTo("/search");
        assertThat(uri.findParameter("query")).contains("what?");
    }

    @Test
    @DisplayName("슬래시로 시작하지 않는 URI는 예외를 던진다")
    void rejectUriNotStartingWithSlash() {
        // expect
        assertThatThrownBy(() -> new RequestUri("login"))
                .isInstanceOf(HttpParseException.class);
    }
}
