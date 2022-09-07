package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.util.FileReader;

class ResourceControllerTest {

    private final ResourceController controller = new ResourceController();

    @Test
    @DisplayName("get 요청시 해당 리소스를 반환한다.")
    void get() throws IOException {
        // given
        HttpRequest request = HttpRequestGenerator.generate("GET", "/index.html");

        // when
        HttpResponse response = controller.doGet(request);

        // then
        String responseString = response.toResponseString();

        assertThat(responseString).contains(HttpStatus.OK.toResponseString());
        assertThat(responseString).contains(FileReader.read("/index.html"));
    }

    @Test
    @DisplayName("post 요청시 404.html을 반환한다.")
    void post() throws IOException {
        // given
        HttpRequest request = HttpRequestGenerator.generate("POST", "/index.html");

        // when
        HttpResponse response = controller.doPost(request);

        // then
        String responseString = response.toResponseString();

        assertThat(responseString).contains(HttpStatus.NOT_FOUND.toResponseString());
        assertThat(responseString).contains(FileReader.read("/404.html"));
    }

}
