package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.service.UserService;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterServletTest {

    private RegisterServlet registerServlet;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        registerServlet = new RegisterServlet(userService);
    }

    @DisplayName("POST /register 요청 시 account, email, password가 유효하면 유저를 저장하고 index.html을 응답한다.")
    @Test
    void doPost() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody("account=jerry&email=jerry@gmail.com&password=password");
        HttpRequest request = new HttpRequest("POST /register HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        registerServlet.service(request, response);

        assertAll(
                () -> assertThatCode(() -> userService.find("jerry", "password"))
                        .doesNotThrowAnyException(),
                () -> {
                    String actual = response.getBody();
                    final URL resource = getClass().getClassLoader().getResource("static/index.html");
                    String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
                    assertThat(actual).isEqualTo(expect);
                }
        );
    }

    @DisplayName("GET /register 요청 시 register.html 응답한다.")
    @Test
    void doGet() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Host", "localhost:8080");
        headers.add("Connection", "keep-alive");
        RequestBody body = new RequestBody();
        HttpRequest request = new HttpRequest("GET /register HTTP/1.1", headers, body);
        HttpResponse response = new HttpResponse();

        registerServlet.service(request, response);

        String actual = response.getBody();

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expect = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expect);
    }
}
