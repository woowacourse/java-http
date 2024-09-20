package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticResourceServletTest {

    private StaticResourceServlet staticResourceServlet;

    @BeforeEach
    void setUp() {
        staticResourceServlet = new StaticResourceServlet();
    }

    @DisplayName("존재하는 정적 리소스에 대한 GET 요청 시 정적 리소스를 응답한다.")
    @Test
    void doGet() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody();
        HttpRequest request = new HttpRequest("GET /401.html HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        staticResourceServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("존재하지 않는 정적 리소스에 대한 GET 요청 시 404.html을 응답한다.")
    @Test
    void doGet2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody();
        HttpRequest request = new HttpRequest("GET /402.html HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        staticResourceServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }
}
