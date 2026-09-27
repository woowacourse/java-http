package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.Controller;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class ControllerTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void 경로에_맞는_컨트롤러를_반환한다() throws Exception {
        assertThat(requestMapping.getController(get("/"))).isInstanceOf(HelloController.class);
        assertThat(requestMapping.getController(get("/login"))).isInstanceOf(LoginController.class);
        assertThat(requestMapping.getController(get("/register"))).isInstanceOf(RegisterController.class);
    }

    @Test
    void 매핑되지_않은_경로는_정적_리소스_컨트롤러가_처리한다() throws Exception {
        assertThat(requestMapping.getController(get("/index.html"))).isInstanceOf(ResourceController.class);
    }

    @Test
    void 지원하지_않는_HTTP_메서드는_405와_Allow_헤더를_응답한다() throws Exception {
        final HttpRequest request = request("POST / HTTP/1.1 \r\n\r\n");

        assertThat(service(request))
                .startsWith("HTTP/1.1 405 Method Not Allowed ")
                .contains("Allow: GET ");
    }

    @Test
    void 정적_리소스_컨트롤러는_리소스를_응답한다() throws Exception {
        assertThat(service(get("/index.html"))).startsWith("HTTP/1.1 200 OK ");
    }

    private HttpRequest get(final String path) throws Exception {
        return request("GET " + path + " HTTP/1.1 \r\n\r\n");
    }

    private HttpRequest request(final String raw) throws Exception {
        return HttpRequest.from(new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)));
    }

    private String service(final HttpRequest request) throws Exception {
        final Controller controller = requestMapping.getController(request);
        final HttpResponse response = new HttpResponse();
        controller.service(request, response);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
