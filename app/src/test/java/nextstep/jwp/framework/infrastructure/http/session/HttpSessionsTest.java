package nextstep.jwp.framework.infrastructure.http.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequest;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestBody;
import nextstep.jwp.framework.infrastructure.http.request.HttpRequestHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpSessions 단위 테스트")
class HttpSessionsTest {

    @DisplayName("HttpRequest 내부 JESSIONID 해당하는 쿠키를 찾아서 HttpSession을 반환한다.")
    @Test
    void get_JSESSIONID_WithinRequest() {
        // given
        List<String> headers = Arrays.asList("GET /login.html HTTP/1.1", "Cookie: JSESSIONID=1");
        HttpRequest httpRequest =
            new HttpRequest(HttpRequestHeader.from(headers), new HttpRequestBody(""));
        HttpSessions.addSession("1");

        // when
        HttpSession session = HttpSessions.getSession(httpRequest);

        // then
        assertThat(session).isNotNull();
    }
}
