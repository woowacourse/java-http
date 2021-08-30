package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.TestUtil;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.ResponseStatus;
import nextstep.jwp.http.request.HttpRequest;
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

        HttpResponse response = controller.run(request);

        assertThat(response.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }

    @DisplayName("css 파일을 읽을 때 text/css 헤더를 포함한다.")
    @Test
    void applyCSSHeader() {
        StaticFileController controller = new StaticFileController();
        HttpRequest request = TestUtil.createRequest("GET /css/test.css HTTP/1.1");

        HttpResponse response = controller.run(request);

        assertThat(response.getHttpHeader().getValueByKey("Content-Type")).isEqualTo("text/css");
    }
}