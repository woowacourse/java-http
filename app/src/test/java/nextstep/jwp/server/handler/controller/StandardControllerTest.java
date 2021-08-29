package nextstep.jwp.server.handler.controller;

import nextstep.jwp.fixture.Fixture;
import nextstep.jwp.http.message.element.Headers;
import nextstep.jwp.http.message.element.HttpStatus;
import nextstep.jwp.http.message.element.cookie.Cookie;
import nextstep.jwp.http.message.element.session.HttpSession;
import nextstep.jwp.http.message.element.session.HttpSessions;
import nextstep.jwp.http.message.element.session.Session;
import nextstep.jwp.http.message.request.HttpRequest;
import nextstep.jwp.http.message.response.HttpResponse;
import nextstep.jwp.http.message.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StandardControllerTest {

    @DisplayName("쿠키를 설정하면 setCookie가 설정된다.")
    @Test
    void doService_cookie() {
        StandardController standardController = new StandardController(new CookieController());
        Response response = standardController.doService(Fixture.getHttpRequest("index.html"));

        assertThat(response.asString()).contains(
                "Set-Cookie",
                "test=test"
        );
    }

    @DisplayName("세션을 설정하면 setCookie에 세션이 설정된다.")
    @Test
    void doService_session() {
        StandardController standardController = new StandardController(new SessionController());
        Response response = standardController.doService(Fixture.getHttpRequest("index.html"));

        assertThat(response.asString()).contains(
                "Set-Cookie",
                "JSESSIONID"
        );
    }

    @DisplayName("세션이 존재하면 새로운 세션을 만들지 않는다.")
    @Test
    void doService_session_already() {
        HttpSession httpSession = new HttpSession();
        HttpSessions.put(httpSession);
        StandardController standardController = new StandardController(new SessionController());
        Headers headers = new Headers();
        headers.putHeader("Cookie", String.format("%s=%s", "JSESSIONID", httpSession.getSessionId()));
        Response response = standardController.doService(Fixture.getHttpRequest("index.html", headers));

        assertThat(response.asString()).doesNotContain("JSESSIONID");
    }

    private static class SessionController implements Controller {

        @Override
        public Response doService(HttpRequest httpRequest) {
            Session session = httpRequest.getSession();
            session.setAttribute("test", "test");
            return HttpResponse.status(HttpStatus.OK, "index.html");
        }

        @Override
        public boolean isSatisfiedBy(HttpRequest httpRequest) {
            return true;
        }
    }

    private static class CookieController implements Controller {

        @Override
        public Response doService(HttpRequest httpRequest) {
            Cookie cookie = httpRequest.getCookie();
            cookie.put("test", "test");

            return HttpResponse.status(HttpStatus.OK, "index.html");
        }

        @Override
        public boolean isSatisfiedBy(HttpRequest httpRequest) {
            return true;
        }
    }
}
