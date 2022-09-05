package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestBody;
import org.apache.coyote.http11.HttpRequestHeader;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private static final Manager MANAGER = new SessionManager();
    private static final HttpRequestBody EMPTY_REQUEST_BODY = new HttpRequestBody("");
    private static final HttpRequestHeader EMPTY_REQUEST_HEADER = new HttpRequestHeader(List.of());

    @DisplayName(value = "쿼리 스트링이 없는 경우 200 반환")
    @Test
    void login_page() {
        // given
        final HttpRequest request = new HttpRequest("GET /login HTTP/1.1 ", EMPTY_REQUEST_HEADER, EMPTY_REQUEST_BODY);
        final LoginController loginController = new LoginController(MANAGER);

        final String expected = "HTTP/1.1 200 OK ";

        // when
        final HttpResponse response = loginController.service(request);

        // then
        assertThat(response.generateResponse()).contains(expected);
    }

    @DisplayName(value = "로그인 성공 시 302 반환")
    @Test
    void login_success() {
        // given
        final HttpRequest request = new HttpRequest("POST /login HTTP/1.1 ",
                EMPTY_REQUEST_HEADER, new HttpRequestBody("account=gugu&password=password"));
        final LoginController loginController = new LoginController(MANAGER);

        final String expectedStatusCode = "HTTP/1.1 302";
        final String expectedLocation = "Location: /index.html ";

        // when
        final HttpResponse response = loginController.service(request);
        final String actual = response.generateResponse();

        // then
        assertThat(actual).contains(expectedStatusCode);
        assertThat(actual).contains(expectedLocation);
    }

    @DisplayName(value = "로그인 실패하는 경우 401 반환")
    @Test
    void login_failed() {
        // given
        final HttpRequest request = new HttpRequest("POST /login HTTP/1.1 ",
                EMPTY_REQUEST_HEADER, new HttpRequestBody("account=gugu&password=notPassword"));
        final LoginController loginController = new LoginController(MANAGER);

        final String expected = "HTTP/1.1 401";

        // when
        final HttpResponse response = loginController.service(request);

        // then
        assertThat(response.generateResponse()).contains(expected);
    }
}
