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

        HttpResponse httpResponse = controller.doGet(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }

    @DisplayName("POST 요청이 들어오면 404 에러 페이지로 리다이렉트한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/401.html", "/404.html", "/500.html"})
    void doPostError(String input) {
        PageRenderController controller = new PageRenderController();
        HttpRequest request = TestUtil.createRequest("POST " + input + " HTTP/1.1");

        HttpResponse httpResponse = controller.doPost(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/404.html");
    }
}