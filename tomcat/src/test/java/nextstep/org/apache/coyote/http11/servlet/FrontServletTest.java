package nextstep.org.apache.coyote.http11.servlet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.servlet.FrontServlet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FrontServletTest {

    @DisplayName("프론트 컨트롤러에서의 /경로 맵핑을 확인한다.")
    @Test
    void service_root_request() throws IOException, URISyntaxException {
        // given
        final String firstLine = "GET / HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");
        final FrontServlet frontServlet = new FrontServlet();
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);
        final HttpResponse httpResponse = new HttpResponse();

        // when
        frontServlet.service(httpRequest, httpResponse);

        // then
        assertAll(
                () -> assertThat(httpResponse.getHttpHeaders().getContentLength()).isEqualTo("12"),
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }
    
    @DisplayName("프론트 컨트롤러에서의 /login 경로 맵핑을 확인한다.")
    @Test
    void service_login_request() throws IOException, URISyntaxException {
        // given
        final String firstLine = "GET /login HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");
        final FrontServlet frontServlet = new FrontServlet();
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);
        final HttpResponse httpResponse = new HttpResponse();
        
        // when
        frontServlet.service(httpRequest, httpResponse);
        
        // then
        assertAll(
                () -> assertThat(httpResponse.getViewName().get()).isEqualTo("login"),
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }

    @DisplayName("프론트 컨트롤러에서의 /register 경로 맵핑을 확인한다.")
    @Test
    void service_register_request() throws URISyntaxException, IOException {
        // given
        final String firstLine = "GET /register HTTP/1.1 ";
        final List<String> lines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");
        final FrontServlet frontServlet = new FrontServlet();
        final HttpRequest httpRequest = HttpRequest.from(firstLine, lines, null);
        final HttpResponse httpResponse = new HttpResponse();

        // when
        frontServlet.service(httpRequest, httpResponse);


        // then
        assertAll(
                () -> assertThat(httpResponse.getViewName().get()).isEqualTo("register"),
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK)
        );
    }
}
