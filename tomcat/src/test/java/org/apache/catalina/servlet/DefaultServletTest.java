package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DefaultServletTest {
    private final DefaultServlet servlet;

    public DefaultServletTest() {
        this.servlet = DefaultServlet.getInstance();
    }

    @DisplayName("빈 리소스 GET 요청을 보내면 해당 파일을 응답한다")
    @Test
    void getEmptyResource() throws URISyntaxException {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setPath("/");
        HttpResponse response = new HttpResponse();

        // when
        servlet.service(request, response);

        // then
        String body = "Hello world!";
        assertThat(response.getBytes())
                .contains(body.getBytes());
    }

    @DisplayName("static 리소스 GET 요청을 보내면 해당 파일을 응답한다")
    @Test
    void getStaticResource() throws URISyntaxException, IOException {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setPath("/index.html");
        HttpResponse response = new HttpResponse();

        // when
        servlet.service(request, response);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String file = new String(Files.readAllBytes(Paths.get(resource.toURI())));
        assertAll(
                () -> assertThat(response.getResourceName())
                        .isEqualTo("/index.html"),
                () -> assertThat(response.getBytes())
                        .contains(file.getBytes())
        );
    }

    @DisplayName("요청한 리소스에 따라 타입을 응답한다")
    @Test
    void getCssResource() throws URISyntaxException {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setPath("/style.css");
        HttpResponse response = new HttpResponse();

        // when
        servlet.service(request, response);

        // then
        String contentType = "Content-Type: text/css";
        assertThat(response.getBytes())
                .contains(contentType.getBytes());
    }

    @DisplayName("요청에 JSESSIONID가 없으면 응답 쿠키에 JSESSIONID를 포함한다")
    @Test
    void respondJSessionId() throws URISyntaxException {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setPath("/index.html");
        HttpResponse response = new HttpResponse();

        // when
        servlet.service(request, response);

        // then
        String contentType = "Cookie: JSESSIONID=";
        assertThat(response.getBytes())
                .contains(contentType.getBytes());
    }
}
