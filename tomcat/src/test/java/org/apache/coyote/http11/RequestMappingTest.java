package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    void findsControllerByRequestPath() {
        //given
        Controller loginController = (request, response) -> {
        };
        RequestMapping mapping = new RequestMapping(Map.of("/login", loginController));

        //when
        HttpRequest request = new HttpRequest(
                new RequestLine("GET", "/login", "", "HTTP/1.1"), Map.of(), "");

        //then
        assertThat(mapping.getController(request)).isSameAs(loginController);
    }

    @Test
    void returnsNullForUnknownPath() {
        //given
        RequestMapping mapping = new RequestMapping(Map.of());

        //when
        HttpRequest request = new HttpRequest(
                new RequestLine("GET", "/missing", "", "HTTP/1.1"), Map.of(), "");

        //then
        assertThat(mapping.getController(request)).isNull();
    }
}
