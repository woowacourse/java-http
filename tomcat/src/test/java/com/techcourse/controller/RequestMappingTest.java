package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void 로그인_경로는_로그인_페이지를_응답한다() throws Exception {
        assertThat(serve(get("/login"))).contains("<title>로그인</title>");
    }

    @Test
    void 로그인한_상태에서_login_html로_직접_접근하면_index로_리다이렉트한다() throws Exception {
        String sessionId = newSessionId();
        serve(post("/login", "account=gugu&password=password", sessionId));

        assertThat(serve(get("/login.html", sessionId)))
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");
    }

    @Test
    void 회원가입_html_경로는_회원가입_페이지를_응답한다() throws Exception {
        assertThat(serve(get("/register.html"))).contains("<title>회원가입</title>");
    }

    @Test
    void 매핑되지_않은_경로는_정적_파일로_응답한다() throws Exception {
        assertThat(serve(get("/css/styles.css"))).contains("Content-Type: text/css;charset=utf-8 ");
    }

    private HttpRequest get(String path) {
        return get(path, newSessionId());
    }

    private HttpRequest get(String path, String sessionId) {
        return HttpRequest.from(List.of("GET " + path + " HTTP/1.1"), "").withSessionId(sessionId);
    }

    private HttpRequest post(String path, String body, String sessionId) {
        return HttpRequest.from(List.of("POST " + path + " HTTP/1.1"), body).withSessionId(sessionId);
    }

    private String newSessionId() {
        Session session = SessionManager.getInstance().findOrCreate(null);
        return session.getId();
    }

    private String serve(HttpRequest request) throws Exception {
        HttpResponse response = HttpResponse.empty();
        requestMapping.service(request, response);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.writeTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
