package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.TestUtil;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PageRenderControllerTest {

    @DisplayName("페이지를 요청하면 OK 응답을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/register", "/login", "/401.html", "/404.html", "/500.html"})
    void doService(String input) {
        PageRenderController controller = new PageRenderController();
        HttpRequest request = TestUtil.createRequest("GET " + input + " HTTP/1.1");

        HttpResponse httpResponse = controller.run(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }
}