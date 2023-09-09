package nextstep.jwp.ui;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.http.HttpResponse;
import java.util.Map;
import org.apache.coyote.http.HeaderKey;
import org.apache.coyote.http.MediaType;
import org.apache.coyote.http.request.ContentType;
import org.apache.coyote.util.FileUtil;
import org.junit.jupiter.api.Test;
import support.HttpClient;

class LoginIntegrationTest extends IntegrationTest {

    @Test
    void 로그인_안되어있을때_로그인_페이지_얻기() {
        HttpResponse<String> response = HttpClient.send(port, "/login");

        assertThat(response.body()).isEqualTo(FileUtil.readStaticFile("static/login.html"));
    }

    @Test
    void 로그인_하기() {
        HttpResponse<String> response = HttpClient.send(port, "/login", "account=gugu&password=password",
            ContentType.of(MediaType.APPLICATION_X_WWW_FORM_URL_ENCODED.value));

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(302),
            () -> assertThat(response.headers().firstValue(HeaderKey.LOCATION.value)).isPresent()
                                                                                     .hasValue("/index.html")
        );
    }

    @Test
    void 로그인_실패시_401_응답한다() {
        HttpResponse<String> response = HttpClient.send(port, "/login", "account=gugu1&password=password",
            ContentType.of(MediaType.APPLICATION_X_WWW_FORM_URL_ENCODED.value));

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(302),
            () -> assertThat(response.headers().firstValue(HeaderKey.LOCATION.value)).isPresent()
                                                                                     .hasValue("/401.html")
        );
    }

    @Test
    void 로그인_되어있을때_로그인_페이지_얻으면_index로_리다이렉트된다() {
        HttpResponse<String> loginResponse = HttpClient.send(port, "/login", "account=gugu&password=password",
            ContentType.of(MediaType.APPLICATION_X_WWW_FORM_URL_ENCODED.value));
        String cookie = loginResponse.headers().firstValue(HeaderKey.SET_COOKIE.value).get();

        HttpResponse<String> response = HttpClient.send(port, "/login", Map.of(HeaderKey.COOKIE.value, cookie));

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(302),
            () -> assertThat(response.headers().firstValue(HeaderKey.LOCATION.value)).isPresent()
                                                                                     .hasValue("/index.html")
        );
    }
}
