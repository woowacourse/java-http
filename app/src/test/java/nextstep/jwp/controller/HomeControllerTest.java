package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import nextstep.jwp.TestFixture;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.ResponseHeaders;
import nextstep.jwp.http.request.HttpHeaders;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    private final Controller controller = new HomeController();

    @DisplayName("인덱스 페이지 - 응답 성공1")
    @Test
    void doGet() throws Exception {
        //given
        final HttpRequest httpRequest = TestFixture.getHttpRequest(HttpMethod.GET, "/");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        HttpSession httpSession = httpRequest.getSession();
        httpSession.removeAttribute("user");

        //when
        controller.service(httpRequest, httpResponse);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getBody()).isEqualTo(file);
    }

    @DisplayName("인덱스 페이지 - 응답 성공2")
    @Test
    void doGet2() throws Exception {
        //given
        final HttpRequest httpRequest = TestFixture.getHttpRequest(HttpMethod.GET, "/index.html");
        final HttpResponse httpResponse = new HttpResponse(new ResponseHeaders());

        HttpSession httpSession = httpRequest.getSession();
        httpSession.removeAttribute("user");

        //when
        controller.service(httpRequest, httpResponse);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String file = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(httpResponse.getBody()).isEqualTo(file);
    }
}
