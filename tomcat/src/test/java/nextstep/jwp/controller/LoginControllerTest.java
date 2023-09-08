package nextstep.jwp.controller;

import static org.apache.coyote.http11.HttpHeaders.LOCATION;
import static org.apache.coyote.http11.HttpStatus.FOUND;
import static org.apache.coyote.http11.HttpStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import nextstep.jwp.FileFinder;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestLine;
import org.apache.coyote.http11.HttpResponse;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Nested
    class 로그인_요청시 {

        @Test
        void 로그인을_성공한다() throws Exception {
            // given
            HttpRequest request = createRequest(HttpMethod.POST, "account=gugu&password=password");
            HttpResponse response = new HttpResponse();

            // when
            loginController.doPost(request, response);

            // then
            String sessionKey = response.getCookie().get("JSESSIONID");
            assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(FOUND),
                () -> assertThat(response.getCookie()).containsKey("JSESSIONID"),
                () -> assertUserInSession(request, sessionKey)
            );
        }

        @Test
        void 존재하지않는_회원이면_unauthorized_페이지로_redirect() {
            // given
            HttpRequest request = createRequest(HttpMethod.POST, "account=hi&password=password");
            HttpResponse response = new HttpResponse();

            // when
            loginController.doPost(request, response);

            // then
            assertThat(response.getHttpStatus()).isEqualTo(FOUND);
            assertThat(response.getHeaders().get(LOCATION)).isEqualTo("/401.html");
        }

        @Test
        void 비밀번호가_틀리면_unauthorized_페이지로_redirect() throws Exception {
            // given
            HttpRequest request = createRequest(HttpMethod.POST, "account=hi&password=fail");
            HttpResponse response = new HttpResponse();

            // when
            loginController.doPost(request, response);

            // then
            assertThat(response.getHttpStatus()).isEqualTo(FOUND);
            assertThat(response.getHeaders().get(LOCATION)).isEqualTo("/401.html");
        }
    }

    @Test
    void 로그인_페이지로_요청() throws Exception {
        // given
        HttpRequest request = createRequest(HttpMethod.GET, "");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.getHttpStatus()).isEqualTo(OK);
        assertThat(response.getBody()).isEqualTo(FileFinder.getFileContent("/login.html"));
    }

    private ObjectAssert<Object> assertUserInSession(HttpRequest request, String sessionKey) {
        return assertThat(request.getSessionManager().findSession(sessionKey).getAttribute("user")).isNotNull();
    }

    private HttpRequest createRequest(HttpMethod method, String body) {
        return new HttpRequest(
            new HttpRequestLine(method, "/register", "version"),
            null,
            Map.of(),
            body
        );
    }
}
