package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.TestUtil;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ControllerContainerTest {

    @DisplayName("PageRenderController를 찾는다")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/401.html", "/500.html", "/404.html"})
    void findPageRenderController(String input) {
        String firstLine = "GET " + input + " HTTP/1.1";
        HttpRequest httpRequest = TestUtil.createRequest(firstLine);

        assertThat(ControllerContainer.findController(httpRequest)).isInstanceOf(PageRenderController.class);
    }

    @DisplayName("LoginContoller를 찾는다.")
    @Test
    void findLoginController() {
        String firstLine = "POST /login HTTP/1.1";
        HttpRequest httpRequest = TestUtil.createRequest(firstLine);

        assertThat(ControllerContainer.findController(httpRequest)).isInstanceOf(LoginController.class);
    }

    @DisplayName("RegisterController를 찾는다.")
    @Test
    void findRegisterController() {
        String firstLine = "POST /register HTTP/1.1";
        HttpRequest httpRequest = TestUtil.createRequest(firstLine);

        assertThat(ControllerContainer.findController(httpRequest)).isInstanceOf(RegisterController.class);
    }

    @DisplayName("StaticFileContoller를 찾는다.")
    @ParameterizedTest
    @ValueSource(strings = {"/js/test", "/css/test", "/assets/test"})
    void findStaticFileController(String input) {
        String firstLine = "GET " + input + " HTTP/1.1";
        HttpRequest httpRequest = TestUtil.createRequest(firstLine);

        assertThat(ControllerContainer.findController(httpRequest)).isInstanceOf(StaticFileController.class);
    }
}