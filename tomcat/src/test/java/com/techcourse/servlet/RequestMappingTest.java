package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.Controller;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.request.HttpUrl;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private RequestMapping requestMapping;

    @BeforeEach
    void setUp() {
        Map<String, Controller> pathControllerMapper = Map.of(
                "/found", (request, response) -> response.setStatus(HttpStatus.FOUND),
                "/not-found", (request, response) -> response.setStatus(HttpStatus.NOT_FOUND)
        );
        requestMapping = new RequestMapping(pathControllerMapper);
    }

    @Test
    void getController_whenPathExists_shouldReturnController() {
        HttpRequest request = createRequest("/found");

        Optional<Controller> controller = requestMapping.getController(request);

        assertThat(controller).isPresent();
    }

    @Test
    void getController_whenPathDoesNotExist_shouldReturnEmpty() {
        HttpRequest request = createRequest("/not-exist");

        Optional<Controller> controller = requestMapping.getController(request);

        assertThat(controller).isEmpty();
    }

    private HttpRequest createRequest(String url) {
        HttpRequestStartLine startLine = new HttpRequestStartLine(HttpMethod.GET, new HttpUrl(url));
        return new HttpRequest(startLine, new HttpRequestHeaders(), new HttpRequestBody());
    }
}
