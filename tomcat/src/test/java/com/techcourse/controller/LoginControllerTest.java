package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestHeaders;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private static final String FORM_URLENCODED = "Content-Type: application/x-www-form-urlencoded";
    private static final Pattern SESSION_ID = Pattern.compile("Set-Cookie: JSESSIONID=([^ \r\n]+)");

    private final Controller controller = new LoginController();

    @Test
    void 로그인하지_않았으면_로그인_페이지를_응답한다() throws Exception {
        final String message = service(get(List.of()));

        assertThat(message).startsWith("HTTP/1.1 200 OK ");
        assertThat(message).contains("<title>로그인</title>");
    }

    @Test
    void 쿠키의_세션이_서버에_없으면_로그인_페이지를_응답한다() throws Exception {
        final String message = service(get(List.of("Cookie: JSESSIONID=unknown-session-id")));

        assertThat(message).startsWith("HTTP/1.1 200 OK ");
        assertThat(message).contains("<title>로그인</title>");
    }

    @Test
    void 이미_로그인했으면_index로_리다이렉트한다() throws Exception {
        final Session session = SessionManager.INSTANCE.createSession();
        session.setAttribute("user", InMemoryUserRepository.findByAccount("gugu").orElseThrow());

        final String message = service(get(List.of("Cookie: JSESSIONID=" + session.getId())));

        assertThat(message).startsWith("HTTP/1.1 302 Found ");
        assertThat(message).contains("Location: /index.html ");
    }

    @Test
    void 로그인에_성공하면_세션에_사용자를_담고_쿠키로_세션_ID를_보낸다() throws Exception {
        final String message = service(post("account=gugu&password=password"));

        assertThat(message).startsWith("HTTP/1.1 302 Found ");
        assertThat(message).contains("Location: /index.html ");

        final Matcher matcher = SESSION_ID.matcher(message);
        assertThat(matcher.find()).isTrue();
        final Session session = SessionManager.INSTANCE.findSession(matcher.group(1));
        assertThat(session.getAttribute("user")).isNotNull();
    }

    @Test
    void 비밀번호가_틀리면_401_페이지로_리다이렉트한다() throws Exception {
        final String message = service(post("account=gugu&password=wrong"));

        assertThat(message).contains("Location: /401.html ");
        assertThat(message).doesNotContain("Set-Cookie");
    }

    @Test
    void 값이_비어_있으면_401_페이지로_리다이렉트한다() throws Exception {
        final String message = service(post("account=&password="));

        assertThat(message).contains("Location: /401.html ");
    }

    private HttpRequest get(final List<String> headerLines) {
        return HttpRequest.of(RequestLine.from("GET /login HTTP/1.1"), RequestHeaders.from(headerLines), "");
    }

    private HttpRequest post(final String body) {
        return HttpRequest.of(RequestLine.from("POST /login HTTP/1.1"), RequestHeaders.from(List.of(FORM_URLENCODED)), body);
    }

    private String service(final HttpRequest request) throws Exception {
        final HttpResponse response = new HttpResponse();
        controller.service(request, response);
        return new String(response.getBytes(), UTF_8);
    }
}
