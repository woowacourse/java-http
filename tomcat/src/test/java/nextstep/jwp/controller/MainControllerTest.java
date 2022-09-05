package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MainControllerTest {

    private final MainController controller = new MainController();

    @Test
    @DisplayName("get 요청시 Hello world!를 반환한다.")
    void get() throws IOException {
        // given
        HttpRequest request = HttpRequestGenerator.generate("GET", "/");

        // when
        HttpResponse response = controller.doService(request);

        // then
        String responseString = response.toResponseString();

        assertThat(responseString).contains("200 OK");
        assertThat(responseString).contains("Hello world!");
    }

}
