package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class DefaultControllerTest {

    private final DefaultController defaultController = DefaultController.instance();

    @Test
    void 정적_파일_접근_테스트() throws Exception {
        // given
        String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        HttpResponse httpResponse = defaultController.service(httpRequest);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        assert resource != null;
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String actual = httpResponse.toResponseFormat();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains("Content-Type: text/css"),
                () -> assertThat(actual).contains(expectedResponseBody)
        );
    }
}
