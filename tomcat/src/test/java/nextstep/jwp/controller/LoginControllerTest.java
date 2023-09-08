package nextstep.jwp.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.coyote.http11.ContentType;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpPath;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseBody;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.HttpMethod.GET;
import static org.apache.coyote.http11.HttpStatus.OK;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;
import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private LoginController loginController = new LoginController();

    @Test
    void get() throws Exception {
        //given
        final var request = HttpRequest.builder()
                .method(GET)
                .path(new HttpPath("/login"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive"))
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        loginController.service(request, response);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String messageBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final var body = ResponseBody.of(new ContentType("text/html"), messageBody);
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(OK)
                .body(body)
                .build();

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }


    @Test
    void post() throws Exception {
        //given
        final var request = HttpRequest.builder()
                .method(HttpMethod.POST)
                .path(new HttpPath("/login"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive", "Content-Length", "30",
                        "Content-Type", "application/x-www-form-urlencoded"))
                .messageBody("account=gugu&password=password")
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        loginController.service(request, response);

        //then
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .header("Set-Cookie", "JSESSIONID=.*")
                .build();

        assertThat(response.buildResponse())
                .containsPattern(expected.buildResponse());
    }

    @Test
    void postUnauthorized() throws IOException {
        //given
        final var request = HttpRequest.builder()
                .method(HttpMethod.POST)
                .path(new HttpPath("/login"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive", "Content-Length", "35",
                        "Content-Type", "application/x-www-form-urlencoded"))
                .messageBody("account=gugu&password=wrongPassword")
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        loginController.service(request, response);

        //then
        final var URI = getClass().getClassLoader().getResource("static/401.html");
        String messageBody = new String(Files.readAllBytes(new File(URI.getFile()).toPath()));
        final var body = ResponseBody.of(new ContentType("text/html"), messageBody);
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .body(body)
                .build();

        assertThat(response)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void setCookie() throws Exception {
        //given
        final var request = HttpRequest.builder()
                .version(HTTP_1_1)
                .method(HttpMethod.POST)
                .path(new HttpPath("/login"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive", "Content-Length", "30",
                        "Content-Type", "application/x-www-form-urlencoded"))
                .messageBody("account=gugu&password=password")
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        loginController.service(request, response);

        //then
        final var expected = HttpResponse.builder()
                .httpVersion(HTTP_1_1)
                .httpStatus(HttpStatus.FOUND)
                .header("Location", "/index.html")
                .header("Set-Cookie", "JSESSIONID=.*")
                .build();

        assertThat(response.buildResponse())
                .containsPattern(expected.buildResponse());
    }

    @Test
    void alreadyLoggedIn() throws Exception {
        //given
        final var sessionId = createSessionId();
        final var request = HttpRequest.builder()
                .method(GET)
                .version(HTTP_1_1)
                .path(new HttpPath("/login"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive"))
                .cookie(Map.of("JSESSIONID", sessionId))
                .build();

        final var response = HttpResponse.prepareFrom(request);

        //when
        loginController.service(request, response);

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

    private String createSessionId() throws Exception {
        final var request = HttpRequest.builder()
                .method(HttpMethod.POST)
                .path(new HttpPath("/login"))
                .headers(Map.of("Host", "localhost:8080", "Connection", "keep-alive", "Content-Length", "30",
                        "Content-Type", "application/x-www-form-urlencoded"))
                .messageBody("account=gugu&password=password")
                .build();
        final var response = HttpResponse.prepareFrom(request);

        //when
        loginController.service(request, response);

        return response.buildResponse().split("JSESSIONID=")[1].split(";")[0].split(" ")[0];
    }

}
