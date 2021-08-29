package nextstep.jwp.framework.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.HttpMethod;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpVersion;
import nextstep.jwp.framework.http.RequestLine;
import nextstep.jwp.webserver.controller.ErrorController;
import nextstep.jwp.webserver.controller.WelcomePageController;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerMappingTest {

    @DisplayName("요청 URI와 일치하는 컨트롤러가 존재할 경우 해당 컨트롤러 반환 테스트")
    @Test
    void findController() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();

        // when
        final Controller controller = ControllerMapping.findController(httpRequest);

        // then
        assertThat(controller).isExactlyInstanceOf(WelcomePageController.class);
    }

    @DisplayName("요청 URI와 일치하는 컨트롤러가 존재하지 않을 경우 에러 컨트롤러 반환 테스트")
    @Test
    void returnErrorControllerIfNotMatch() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/not-match", HttpVersion.HTTP_1_1);
        HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();

        // when
        final Controller controller = ControllerMapping.findController(httpRequest);

        // then
        assertThat(controller).isExactlyInstanceOf(ErrorController.class);
    }

    @DisplayName("요청 URI는 일치하지만 메소드가 일치하지 않을 경우 에러 컨트롤러 반환 테스트")
    @Test
    void returnErrorControllerIfMethodNotMatch() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.POST, "/", HttpVersion.HTTP_1_1);
        HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();

        // when
        final Controller controller = ControllerMapping.findController(httpRequest);

        // then
        assertThat(controller).isExactlyInstanceOf(ErrorController.class);
    }
}
