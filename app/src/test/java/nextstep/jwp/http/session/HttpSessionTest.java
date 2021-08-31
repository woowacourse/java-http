package nextstep.jwp.http.session;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestBody;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
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
        final RequestHeaders headers = new RequestHeaders(new HashMap<>());
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
        final RequestLine beforeLoginRequestLine = new RequestLine("GET /login HTTP/1.1");
        final RequestHeaders beforeLoginHeaders = new RequestHeaders(new HashMap<>());
        final RequestBody beforeLoginRequestBody = RequestBody.empty();
        final HttpRequest beforeLoginRequest = new HttpRequest(beforeLoginRequestLine, beforeLoginHeaders, beforeLoginRequestBody);

        final HttpSession beforeLoginSession = beforeLoginRequest.getSession();
        final User user = new User(1L, "inbi", "1234", "inbi@email.com");
        beforeLoginSession.setAttribute("user", user);

        //when
        final RequestLine afterLoginRequestLine = new RequestLine("GET /login HTTP/1.1");
        final RequestHeaders afterLoginHeaders = new RequestHeaders(Map.of("Cookie", "JSESSIONID=" + beforeLoginSession.getId()));
        final RequestBody afterLoginRequestBody = RequestBody.empty();
        final HttpRequest afterLoginRequest = new HttpRequest(afterLoginRequestLine, afterLoginHeaders, afterLoginRequestBody);
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
