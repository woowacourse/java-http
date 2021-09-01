package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.TestUtil;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StaticFileControllerTest {

    @DisplayName("/asset, /js, /css 경로 하위의 파일들을 읽어온다.")
    @ParameterizedTest
    @ValueSource(strings = {"/assets/asset.js", "/js/test.js", "/css/test.css"})
    void readStaticFiles(String input) {
        StaticFileController controller = new StaticFileController();
        HttpRequest request = TestUtil.createRequest("GET " + input + " HTTP/1.1");

        HttpResponse response = controller.doGet(request);

        assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }

    @DisplayName("POST 요청이 들어오면 404에러 페이지로 리다이렉트한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/assets/asset.js", "/js/test.js", "/css/test.css"})
    void doPostError(String input) {
        StaticFileController controller = new StaticFileController();
        HttpRequest request = TestUtil.createRequest("POST " + input + " HTTP/1.1");

        HttpResponse response = controller.doPost(request);

        assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(response.getHttpHeader().getValueByKey("Location")).contains("/404.html");
    }

    @DisplayName("css 파일을 읽을 때 text/css 헤더를 포함한다.")
    @Test
    void applyCSSHeader() {
        StaticFileController controller = new StaticFileController();
        HttpRequest request = TestUtil.createRequest("GET /css/test.css HTTP/1.1");

        HttpResponse response = controller.doGet(request);

        assertThat(response.getHttpHeader().getValueByKey("Content-Type")).isEqualTo("text/css");
    }
}