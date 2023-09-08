package org.apache.coyote.handler;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.FixtureFactory;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultHandlerTest {

    @Test
    @DisplayName("hello world라는 정보가 담긴 response가 생성된다.")
    void doHandle() {
        Request request = FixtureFactory.getGetRequest("/", DEFAULT_HEADERS);
        Response response = new Response();

        DefaultHandler defaultHandler = new DefaultHandler();
        defaultHandler.response(request, response);

        String expected = "Hello world!";

        assertThat(response.getResponseBytes()).contains(expected.getBytes());
    }
}
