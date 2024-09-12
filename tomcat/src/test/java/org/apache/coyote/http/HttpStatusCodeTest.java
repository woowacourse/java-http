package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class HttpStatusCodeTest {

    @Test
    void OK_상태코드를_가져올_수_있다() {
        // when
        HttpStatusCode statusCode = HttpStatusCode.OK;

        // then
        assertThat(statusCode.getValue()).isEqualTo("200 OK");
    }

    @Test
    void FOUND_상태코드를_가져올_수_있다() {
        // when
        HttpStatusCode statusCode = HttpStatusCode.FOUND;

        // then
        assertThat(statusCode.getValue()).isEqualTo("302 Found");
    }
}
