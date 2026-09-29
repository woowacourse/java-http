package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void redirect() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.redirect("/index.html");

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeaders())
                .containsEntry("Location", "/index.html");
    }
}
