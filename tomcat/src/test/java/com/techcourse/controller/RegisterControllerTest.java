package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.request.RequestHeaders;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private static final String FORM_URLENCODED = "Content-Type: application/x-www-form-urlencoded";

    private final Controller controller = new RegisterController();

    @Test
    void 회원가입_페이지를_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.of(
                RequestLine.from("GET /register HTTP/1.1"), RequestHeaders.from(List.of()), "");

        final String message = service(request);

        assertThat(message).startsWith("HTTP/1.1 200 OK ");
        assertThat(message).contains("<title>회원가입</title>");
    }

    @Test
    void 가입하면_사용자를_저장하고_index로_리다이렉트한다() throws Exception {
        final String message = service(post("account=register-success&password=pw&email=a%40b.com"));

        assertThat(message).startsWith("HTTP/1.1 302 Found ");
        assertThat(message).contains("Location: /index.html ");
        assertThat(InMemoryUserRepository.findByAccount("register-success")).isPresent();
    }

    @Test
    void 값이_비어_있으면_저장하지_않고_회원가입_페이지를_응답한다() throws Exception {
        final String message = service(post("account=register-blank&password=&email="));

        assertThat(message).startsWith("HTTP/1.1 200 OK ");
        assertThat(message).contains("<title>회원가입</title>");
        assertThat(InMemoryUserRepository.findByAccount("register-blank")).isEmpty();
    }

    @Test
    void 이미_있는_계정이면_덮어쓰지_않고_회원가입_페이지를_응답한다() throws Exception {
        final String message = service(post("account=gugu&password=hacked&email=hacker%40x.com"));

        assertThat(message).startsWith("HTTP/1.1 200 OK ");
        assertThat(message).contains("<title>회원가입</title>");
        final User gugu = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
        assertThat(gugu.checkPassword("password")).isTrue();
        assertThat(gugu.checkPassword("hacked")).isFalse();
    }

    private HttpRequest post(final String body) {
        return HttpRequest.of(RequestLine.from("POST /register HTTP/1.1"), RequestHeaders.from(List.of(FORM_URLENCODED)), body);
    }

    private String service(final HttpRequest request) throws Exception {
        final HttpResponse response = new HttpResponse();
        controller.service(request, response);
        return new String(response.getBytes(), UTF_8);
    }
}
