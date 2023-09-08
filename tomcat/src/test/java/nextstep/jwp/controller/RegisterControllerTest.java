package nextstep.jwp.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseBody;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpMethod.POST;
import static org.apache.coyote.http11.HttpStatus.OK;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @Test
    void get() throws Exception {
        //given
        final var request = HttpRequest.builder()
                .method(GET)
                .path(new HttpPath("/register"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive"))
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        registerController.service(request, response);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final var messageBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(OK)
                .body(ResponseBody.of(new ContentType("text/html"), messageBody))
                .build();

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
    @Test
    void post() throws Exception {
        //given
        final var request = HttpRequest.builder()
                .method(POST)
                .path(new HttpPath("/register"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive", "Content-Length", "30",
                        "Content-Type", "application/x-www-form-urlencoded"))
                .messageBody("account=rosie&email=rosie@zipgo.pet&password=password")
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        registerController.service(request, response);

        //then
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .build();

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

}
