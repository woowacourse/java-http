package org.apache.mvc.handlerchain;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class NotFoundHandlerChainTest {

    @Test
    void handle() {
        // given
        NotFoundHandlerChain chain = new NotFoundHandlerChain();

        // when
        HttpResponse response = chain.handle(null);

        // then
        assertThat(response.getAsString()).contains("HTTP/1.1 404 NOT_FOUND");
    }
}
