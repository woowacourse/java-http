package acceptance;

import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.TestHttpUtils;
import support.TestServer;
import util.BiValue;
import util.StringUtil;

import static org.assertj.core.api.Assertions.assertThat;

class LoginAcceptanceTest {
    @Test
    @DisplayName("로그인에 성공하면, 세션을 설정하고 302 상태코드와 index.html 로 리다이렉트한다.")
    void login_success() {
        TestServer.serverStart();
        final SessionManager sessionManager = new SessionManager();

        final String body = "account=gugu&password=password";
        final var result = TestHttpUtils.sendPost(TestServer.getHost(), "/login", body);

        final BiValue<String, String> sessionKeyAndValue = StringUtil.splitBiValue(result.headers()
                .firstValue("set-cookie")
                .get(), "=");


        assertThat(sessionManager.findSession(sessionKeyAndValue.second())).isPresent();
        assertThat(result.headers()
                .firstValue("Location")).contains("/index.html");
        assertThat(result.statusCode()).isEqualTo(302);
    }

    @Test
    @DisplayName("로그인에 실패하면, 401.html 로 리다이렉트한다.")
    void login_failed() {
        TestServer.serverStart();

        final String body = "account=noAccount&password=noPassword";
        final var result = TestHttpUtils.sendPost(TestServer.getHost(), "/login", body);

        assertThat(result.headers()
                .firstValue("Location")).contains("/401.html");
        assertThat(result.statusCode()).isEqualTo(302);

    }
}
