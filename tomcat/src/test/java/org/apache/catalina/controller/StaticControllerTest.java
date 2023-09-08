package org.apache.catalina.controller;

import static org.apache.coyote.http11.common.HttpStatus.NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class
StaticControllerTest {

    private final Controller controller = new StaticController();

    @Test
    void Get_요청시_정적_파일을_반환하도록_설정한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, new Headers(), new RequestBody());
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getRedirect()).isEqualTo("/index.html")
        );
    }

    @Test
    void Get_요청시_uri에_해당하는_파일이_없는경우_404_NOT_FOUND_페이지를_반환하도록_설정한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /helloworld.html HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, new Headers(), new RequestBody());
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(NOT_FOUND),
                () -> assertThat(httpResponse.getRedirect()).isEqualTo("/404.html")
        );
    }
}
