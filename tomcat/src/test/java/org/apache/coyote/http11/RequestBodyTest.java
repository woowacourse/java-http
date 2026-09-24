package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class RequestBodyTest {

    @Test
    void getParameter_success() {
        // given
        final String body =
                "account=gugu%40email.com&password=1234";

        final RequestBody requestBody = new RequestBody(body);

        // when
        final String account = requestBody.getParameter("account");

        // then
        assertThat(account)
                .isEqualTo("gugu@email.com");
    }
}
