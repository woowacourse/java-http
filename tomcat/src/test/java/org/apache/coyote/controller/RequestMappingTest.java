package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class RequestMappingTest extends ControllerTestSupport{

    @Test
    void 잘못된_uri는_index_controller를_반환한다() {
        // given
        final String input = String.join("\r\n",
                "GET /bebe HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        final HttpRequest httpRequest = super.makeHttpRequest(input);

        // when
        final RequestMapping requestMapping = new RequestMapping();
        final Controller actual = requestMapping.getController(httpRequest);

        // then
        assertThat(actual).isInstanceOf(IndexController.class);
    }
}
