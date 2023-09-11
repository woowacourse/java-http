package nextstep.jwp.controller;

import common.http.Request;
import common.http.Response;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static common.Constants.CRLF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HomeControllerTest {

    @Test
    void 응답에_속성을_추가한다() {
        // given
        HomeController homeController = new HomeController();
        Request request = mock(HttpRequest.class);
        Response httpResponse = new HttpResponse();
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");
        // when
        homeController.doGet(request, httpResponse);

        // then
        Assertions.assertThat(httpResponse.toString()).hasToString(
                "HTTP/1.1 200 OK " + CRLF +
                "Content-Length: 12 " + CRLF +
                "Content-Type: text/html;charset=utf-8 " + CRLF + CRLF +
                "Hello world!");
    }
}
