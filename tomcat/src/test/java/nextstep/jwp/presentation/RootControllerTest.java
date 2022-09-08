package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class RootControllerTest {

    private final RootController rootController = RootController.instance();

    @Test
    void index_페이지_접근_테스트() throws Exception {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "");
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes())));
        HttpRequest httpRequest = HttpRequest.parse(bufferedReader);

        // when
        HttpResponse httpResponse = rootController.service(httpRequest);

        // then
        String actual = httpResponse.toResponseFormat();
        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 200 OK"),
                () -> assertThat(actual).contains("Hello world!")
        );
    }
}
