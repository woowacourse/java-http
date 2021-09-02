package nextstep.jwp.http.session;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.request.*;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HttpSession 테스트")
class HttpSessionTest {

    @DisplayName("세션 생성 테스트")
    @Test
    void create() {
        //given
        final RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        final RequestCookie requestCookie = new RequestCookie(new HashMap<>());
        final RequestHeaders headers = new RequestHeaders(new HashMap<>(), requestCookie);
        final RequestBody requestBody = RequestBody.empty();
        final HttpRequest request = new HttpRequest(requestLine, headers, requestBody);

        // when
        final HttpSession session = request.getSession();

        //then
        assertThat(HttpSessions.getSession(session.getId())).isNotEmpty();
    }

    @DisplayName("세션 저장/조회 테스트")
    @Test
    void saveAndFindFromSession() {
        //given
        final RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        final Map<String, String> emptyHeadersExceptCookie = new HashMap<>();

        final RequestCookie beforeLoginEmptyCookie = new RequestCookie(new HashMap<>());
        final RequestHeaders beforeLoginHeaders = new RequestHeaders(emptyHeadersExceptCookie, beforeLoginEmptyCookie);
        final HttpRequest beforeLoginRequest = new HttpRequest(requestLine, beforeLoginHeaders, RequestBody.empty());

        final HttpSession beforeLoginSession = beforeLoginRequest.getSession();
        final User user = new User(1L, "inbi", "1234", "inbi@email.com");
        beforeLoginSession.setAttribute("user", user);

        //when
        final Map<String, String> cookie = Map.of("JSESSIONID", beforeLoginSession.getId());
        final RequestCookie afterLoginCookie = new RequestCookie(cookie);
        final RequestHeaders afterLoginHeaders = new RequestHeaders(emptyHeadersExceptCookie, afterLoginCookie);

        final HttpRequest afterLoginRequest = new HttpRequest(requestLine, afterLoginHeaders, RequestBody.empty());
        final HttpSession afterLoginSession = afterLoginRequest.getSession();

        //then
        assertThat(afterLoginSession.hasAttribute("user")).isTrue();
        final User foundUser = (User) afterLoginSession.getAttribute("user")
                .orElseThrow(() -> new NotFoundException("Session에 user의 값이 존재하지 않습니다."));
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getAccount()).isEqualTo(user.getAccount());
        assertThat(foundUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(foundUser.getEmail()).isEqualTo(user.getEmail());
    }
}
