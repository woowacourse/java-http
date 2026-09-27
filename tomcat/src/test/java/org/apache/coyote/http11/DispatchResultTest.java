package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DispatchResultTest {

    @Test
    void 포워드_결과를_생성한다() {
        final DispatchResult result = DispatchResult.forward(
            HttpStatus.OK,
            "/index.html");

        assertThat(result.type()).isEqualTo(DispatchType.FORWARD);
        assertThat(result.status()).isEqualTo(HttpStatus.OK);
        assertThat(result.path()).isEqualTo("/index.html");
    }

    @Test
    void 리다이렉트_결과를_생성한다() {
        final DispatchResult result = DispatchResult.redirect(
            HttpStatus.FOUND,
            "/index");

        assertThat(result.type()).isEqualTo(DispatchType.REDIRECT);
        assertThat(result.status()).isEqualTo(HttpStatus.FOUND);
        assertThat(result.path()).isEqualTo("/index");
    }
}
