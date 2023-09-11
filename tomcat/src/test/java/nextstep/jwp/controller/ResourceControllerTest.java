package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ResourceControllerTest {

    private final Controller resourceController = new ResourceController();

    @Test
    @DisplayName("ResourceController는 Post 요청을 지원하지 않는다.")
    void resourceControllerNotSupportWhenPostMethod() throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        String expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 FOUND ",
                "Location: /405.html ");

        //when
        resourceController.service(RequestFixture.POST_REQUEST, response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains(expected);
    }

    @ParameterizedTest(name = "ResourceController는 GET 요청시, 요청 path에 따른 정적 파일을 반환한다.")
    @ValueSource(strings = {"/css/styles.css", "/js/scripts.js"})
    void resourceControllerSuccessServiceWhenGetMethod(String path) throws IOException {
        //given
        HttpResponse response = HttpResponse.createEmptyResponse();
        URL url = getClass().getClassLoader().getResource("static" + path);
        String expected = new String(Files.readAllBytes(Path.of(url.getPath())));

        //when
        resourceController.service(RequestFixture.createGetRequestWithURI(path), response);

        //then
        String actual = new String(response.getBytes());
        assertThat(actual).contains("HTTP/1.1 200 OK")
                .contains(expected);
    }

}