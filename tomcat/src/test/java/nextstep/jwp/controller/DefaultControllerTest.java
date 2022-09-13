package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.TestRequest;

class DefaultControllerTest {

    @Test
    @DisplayName("Post 요청이 올 경우 예외를 반환한다.")
    void doPost_exception() {
        // given
        DefaultController controller = new DefaultController();
        HttpRequest request = TestRequest.generateWithUri("/index");

        // when, then
        assertThatThrownBy(() -> controller.doPost(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unsupported Method for this path: /index");
    }

    @Test
    @DisplayName("GET 요청이 올 경우 index.html 파일을 응답한다.")
    void doGet() {
        // given
        final DefaultController controller = new DefaultController();
        final HttpRequest request = TestRequest.generateWithUri("/index");

        // when
        final HttpResponse response = controller.doGet(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 23 ",
            "",
            "default controller test");

        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }
}