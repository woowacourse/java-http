package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.web.QueryParameters;
import org.apache.coyote.http11.web.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("로그인 성공 시, Cookie에 Session을 담아서 반환한다.")
    @Test
    void login_returnsResponseWithCookie() {
        // given
        final QueryParameters queryParameters = QueryParameters.from("account=gugu&password=password");

        // when
        final HttpResponse httpResponse = new LoginController().login(queryParameters);
        final String actual = httpResponse.format();

        // then
        assertThat(actual).contains("Set-Cookie: JSESSIONID=");
    }
}
