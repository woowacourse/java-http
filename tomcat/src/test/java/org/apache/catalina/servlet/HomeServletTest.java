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

class HomeServletTest {

    private HomeServlet homeServlet;

    @BeforeEach
    void setUp() {
        homeServlet = new HomeServlet();
    }

    @DisplayName("GET / 요청 시 index.html 응답한다.")
    @Test
    void doGet() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody();
        HttpRequest request = new HttpRequest("GET / HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        homeServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }
}
