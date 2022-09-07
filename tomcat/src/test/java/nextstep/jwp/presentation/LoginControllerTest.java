package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.support.HttpHeaders;
import org.apache.coyote.http11.support.HttpStartLine;
import org.apache.coyote.http11.web.request.HttpRequest;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Collections;

class LoginControllerTest {

    @DisplayName("로그인 성공 시, Cookie에 Session을 담아서 반환한다.")
    @Test
    void login_returnsResponseWithCookie() {
        // given
        final HttpStartLine httpStartLine =
                HttpStartLine.from(new String[]{"GET", "/login?account=gugu&password=password", "HTTP/1.1"});
        final HttpRequest httpRequest = new HttpRequest(httpStartLine, new HttpHeaders(Collections.emptyMap()), "");

        // when
        final HttpResponse httpResponse = new LoginController().doGet(httpRequest);
        final String actual = httpResponse.format();

        // then
        assertThat(actual).contains("Set-Cookie: JSESSIONID=");
    }
}
