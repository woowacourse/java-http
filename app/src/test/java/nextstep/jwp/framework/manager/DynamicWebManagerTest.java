package nextstep.jwp.framework.manager;

import nextstep.jwp.application.model.User;
import nextstep.jwp.framework.http.common.HttpSession;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class DynamicWebManagerTest {

    private final DynamicWebManager dynamicWebManager = new DynamicWebManager();

    @DisplayName("application 패키지 내의 controller를 매핑하고, 처리가 가능한지 검사할 수 있다.")
    @Test
    void canHandle() {
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.GET, "/"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.GET, "/login"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.POST, "/login"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.GET, "/register"))).isTrue();
        assertThat(dynamicWebManager.canHandle(HttpRequest.of(HttpMethod.POST, "/register"))).isTrue();
    }

    @DisplayName("GET: /는 /index.html을 반환한다.")
    @Test
    void indexPage() {
        final HttpRequest indexRequest = HttpRequest.of(HttpMethod.GET, "/");
        final String result = dynamicWebManager.handle(indexRequest);
        assertThat(result).isEqualTo("/index.html");
    }

    @DisplayName("GET: /login은 세션이 없다면 /login.html을, 있다면 redirect:/index.html을 반환한다.")
    @Test
    void loginGetMethod() {
        final String requestLine = "GET /login HTTP/1.1";
        final HttpRequest loginRequest = HttpRequest.from(requestLine, Collections.emptyMap(), "");
        final String loginWithoutSession = dynamicWebManager.handle(loginRequest);
        assertThat(loginWithoutSession).isEqualTo("/login.html");

        final HttpSession httpSession = loginRequest.generateSession();
        httpSession.setAttribute("user", new User("account", "password", "email"));
        final String loginSession = dynamicWebManager.handle(loginRequest);
        assertThat(loginSession).isEqualTo("redirect:/index.html");
    }

    @DisplayName("POST: /login은 회원인지 검사하고 회원이라면 redirect:/index.html을, 아니면 redirect:/401.html을 반환한다.")
    @Test
    void loginPostMethod() {
        final String requestLine = "POST /login HTTP/1.1";
        final String properRequestBody = "account=gugu&password=password";
        final HttpRequest properLoginRequest = HttpRequest.from(requestLine, Collections.emptyMap(), properRequestBody);
        final String requestResult1 = dynamicWebManager.handle(properLoginRequest);
        assertThat(requestResult1).isEqualTo("redirect:/index.html");

        final String invalidRequestBody = "account=nono&password=nana";
        final HttpRequest InvalidLoginRequest = HttpRequest.from(requestLine, Collections.emptyMap(), invalidRequestBody);
        final String requestResult2 = dynamicWebManager.handle(InvalidLoginRequest);
        assertThat(requestResult2).isEqualTo("redirect:/401.html");
    }

    @DisplayName("GET: /register는 /register.html을 반환한다")
    @Test
    void registerGetMethod() {
        final HttpRequest registerRequest = HttpRequest.of(HttpMethod.GET, "/register");
        final String result = dynamicWebManager.handle(registerRequest);
        assertThat(result).isEqualTo("/register.html");
    }

    @DisplayName("POST: /register는 redirect:/index.html을 반환한다")
    @Test
    void registerPostMethod() {
        final String requestLine = "POST /register HTTP/1.1";
        final String requestBody = "account=joel&password=joel&email=joel@joel.com";
        final HttpRequest registerRequest = HttpRequest.from(requestLine, Collections.emptyMap(), requestBody);
        final String result = dynamicWebManager.handle(registerRequest);
        assertThat(result).isEqualTo("redirect:/index.html");
    }
}
