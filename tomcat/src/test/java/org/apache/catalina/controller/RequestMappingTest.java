package org.apache.catalina.controller;

import nextstep.jwp.presentation.HomePageController;
import org.apache.coyote.HttpFormTestUtils;
import org.apache.coyote.publisher.InputStreamRequestPublisher;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMappingTest {

    @Test
    void 홈_컨트롤러를_가져올_수_있다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(UTF_8));
        final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // when
        final Controller foundController = RequestMapping.getController(request);

        // then
        assertThat(foundController).isInstanceOf(HomePageController.class);
    }
}
