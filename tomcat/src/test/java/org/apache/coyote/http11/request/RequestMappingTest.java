package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.controller.LoginController;

class RequestMappingTest {

    @Test
    @DisplayName("요청에 따라 컨트롤러를 선택한다.")
    void mappingController() {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /login HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: */*")),
                new HttpRequestBody("")
        );
        HttpResponse response = HttpResponse.defaultResponse();
        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(302);
    }

    @Test
    @DisplayName("요청을 처리할 수 있는 컨트롤러가 없는 경우 StaticController를 할당한다.")
    void mappingStaticController() throws IOException {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /index.html HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: */*")),
                new HttpRequestBody("")
        );
        HttpResponse response = HttpResponse.defaultResponse();
        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.getController(request);
        controller.service(request, response);

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(response.getResponseBody()).isEqualTo(expected);
    }
}
