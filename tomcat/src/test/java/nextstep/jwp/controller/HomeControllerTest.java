package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.catalina.controller.Controller;
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
class HomeControllerTest {

    private final Controller controller = new HomeController();

    @Test
    void GET_요청을_하는_경우_OK_응답을_반환한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET / HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, new Headers(), new RequestBody());
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        controller.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getRedirect()).isEqualTo("home.html")
        );
    }
}
