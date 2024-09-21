package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.session.SessionService;
import com.techcourse.service.UserService;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.headers.HeaderName;
import org.apache.coyote.http11.headers.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginServletTest {

    private LoginServlet loginServlet;

    @BeforeEach
    void setUp() {
        loginServlet = new LoginServlet(new UserService(), new SessionService());
    }

    @DisplayName("POST /login 요청 헤더의 쿠키에 JSESSIONID가 없으면 응답 헤더에 Set-Cookie를 반환한다.")
    @Test
    void doPost() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody("account=gugu&password=password");
        HttpRequest request = new HttpRequest("POST /login HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        loginServlet.service(request, response);

        Optional<String> actual = response.getHeaders().findByName(HeaderName.SET_COOKIE);

        assertThat(actual).isPresent();
    }

    @DisplayName("유효한 사용자로 POST /login 요청 시 index.html 응답한다.")
    @Test
    void doPostWithValidUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody("account=gugu&password=password");
        HttpRequest request = new HttpRequest("POST /login HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        loginServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("유효하지 않은 사용자로 POST /login 요청 시 401.html 응답한다.")
    @Test
    void doPostWithInvalidUser() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody("account=gugu&password=wrongpassword");
        HttpRequest request = new HttpRequest("POST /login HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        loginServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }

    @DisplayName("GET /login 요청 시 login.html 응답한다.")
    @Test
    void doGet() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody();
        HttpRequest request = new HttpRequest("GET /login HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        loginServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }
}
