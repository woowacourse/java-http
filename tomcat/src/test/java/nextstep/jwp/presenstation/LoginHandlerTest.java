package nextstep.jwp.presenstation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.handler.ResponseEntity;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.RequestBody;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    private final LoginHandler loginHandler = new LoginHandler();

    @Nested
    @DisplayName("GET /login을 요청하면")
    class Get {

        @Test
        @DisplayName("로그인페이지를 보여준다")
        void naive() {
            ResponseEntity responseEntity = loginHandler.handle(new HttpRequest(HttpMethod.GET, "/login"));
            assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Cookie의 JSESSIONID가 세션에 있는 경우, /index 페이지로 리다이렉팅시킨다.")
        void withSession() {
            String sessionId = "sample-code";
            Session session = new Session(sessionId);
            session.setAttribute("user", new User("user", "password", "email@email.com"));
            SessionManager.add(session);
            HttpCookie cookie = new HttpCookie(Map.of("JSESSIONID", sessionId));

            ResponseEntity responseEntity = loginHandler.handle(
                    new HttpRequest(HttpMethod.GET, "/login", new HttpHeaders(), new RequestBody(), cookie));
            assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.FOUND);
        }
    }

    @Nested
    @DisplayName("POST /login을 요청하면")
    class Post {

        @Test
        @DisplayName("존재하는 회원일 경우 Cookie에 SessionId를 담고 리다이렉팅 시킨다.")
        void success() {
            User user = new User("user", "password", "email@email.com");
            InMemoryUserRepository.save(user);

            RequestBody requestBody = new RequestBody(Map.of("account", "user", "password", "password"));
            ResponseEntity responseEntity = loginHandler.handle(
                    new HttpRequest(HttpMethod.POST, "/login", new HttpHeaders(), requestBody, new HttpCookie())
            );
            assertAll(
                    () -> assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.FOUND),
                    () -> assertThat(responseEntity.getHeaders().get("Set-Cookie")).isNotNull()
            );
        }

        @Test
        @DisplayName("존재하는 회원일 경우 Cookie에 SessionId를 담고 리다이렉팅 시킨다.")
        void fail_noExistUser() {
            RequestBody requestBody = new RequestBody(Map.of("account", "user", "password", "password"));
            ResponseEntity responseEntity = loginHandler.handle(
                    new HttpRequest(HttpMethod.POST, "/login", new HttpHeaders(), requestBody, new HttpCookie())
            );

            assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        }
    }
}
