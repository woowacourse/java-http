package org.apache.controller;

import static org.apache.coyote.FixtureFactory.DEFAULT_HEADERS;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.controller.DefaultController;
import org.apache.coyote.FixtureFactory;
import org.apache.coyote.http11.Protocol;
import org.apache.coyote.request.Request;
import org.apache.coyote.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultControllerTest {

    @Test
    @DisplayName("hello world라는 정보가 담긴 response가 생성된다.")
    void doHandle() throws Exception {
        Request request = FixtureFactory.getHttpGetRequest("/", DEFAULT_HEADERS);
        Response response = new Response(Protocol.HTTP1_1);

        DefaultController defaultController = new DefaultController();
        defaultController.service(request, response);

        String expected = "Hello world!";

        assertThat(response.getResponseBytes()).contains(expected.getBytes());
    }
}
