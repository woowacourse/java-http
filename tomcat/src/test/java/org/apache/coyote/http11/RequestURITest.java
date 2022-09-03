package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.exception.NoSuchQueryParameterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestURITest {

    @DisplayName("RequestURI를 생성한다.")
    @Test
    void RequestURI를_생성한다() {
        // given
        String requestTarget = "/index.html";

        // when
        RequestURI actual = new RequestURI(requestTarget);

        // then
        assertAll(() -> {
            assertThat(actual.getPath()).isEqualTo("/index.html");
            assertThat(actual.isQueryParametersEmpty()).isTrue();
        });
    }

    @DisplayName("requestURI를 받아 path와 query를 분리한다.")
    @Test
    void requestURI를_받아_path와_query를_분리한다() {
        // given
        String requestTarget = "/login?account=mat&password=password";

        // when
        RequestURI actual = new RequestURI(requestTarget);

        // then
        assertAll(() -> {
            assertThat(actual.getPath()).isEqualTo("/login");
            assertThat(actual.getQueryParameterKey("account")).isEqualTo("mat");
            assertThat(actual.getQueryParameterKey("password")).isEqualTo("password");
        });
    }

    @DisplayName("존재하지 않는 query parameter를 조회하는 경우 예외를 던진다.")
    @Test
    void 존재하지_읺는_query_paramter를_조회하는_경우_예외를_던진다() {
        // given
        String requestTarget = "/login?account=mat&password=password";

        // when
        RequestURI actual = new RequestURI(requestTarget);

        // then
        assertThatThrownBy(() -> actual.getQueryParameterKey("mat"))
                .isInstanceOf(NoSuchQueryParameterException.class);
    }
}
