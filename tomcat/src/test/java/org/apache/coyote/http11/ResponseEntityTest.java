package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ResponseEntityTest {

    @Test
    void body만_있으면_http_status_ok가_반환된다() {
        // given
        String responseBody = "eden king";

        // when
        ResponseEntity responseEntity = ResponseEntity.body(responseBody);

        // then
        Assertions.assertAll(
                () -> assertThat(responseEntity.getResponseBody()).isEqualTo("eden king"),
                () -> assertThat(responseEntity.getStatus()).isEqualTo("OK"),
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(200)
        );
    }

    @Test
    void body와_함께_status를_지정할_수_있다() {
        // given
        String responseBody = "eden king";

        // when
        ResponseEntity responseEntity = ResponseEntity.body(responseBody).status(HttpStatus.REDIRECT);

        // then
        Assertions.assertAll(
                () -> assertThat(responseEntity.getResponseBody()).isEqualTo("eden king"),
                () -> assertThat(responseEntity.getStatus()).isEqualTo("REDIRECT"),
                () -> assertThat(responseEntity.getStatusCode()).isEqualTo(302)
        );
    }
}
