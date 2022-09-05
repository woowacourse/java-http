package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void request_body를_파싱한다() {
        // given
        String rawRequestBody = "name=eden&nickName=king";

        // when
        RequestBody requestBody = RequestBody.of(rawRequestBody);

        // then
        Assertions.assertAll(
                () -> assertThat(requestBody.get("name")).isEqualTo("eden"),
                () -> assertThat(requestBody.get("nickName")).isEqualTo("king")
        );
    }
}
