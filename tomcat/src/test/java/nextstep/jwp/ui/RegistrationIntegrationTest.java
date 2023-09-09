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

class RegistrationIntegrationTest extends IntegrationTest {
    @Test
    void 회원가입_페이지_조회기능() {
        HttpResponse<String> response = HttpClient.send(port, "/register");

        assertThat(response.body()).isEqualTo(FileUtil.readStaticFile("static/register.html"));
    }

    @Test
    void 회원가입_하기() {
        HttpResponse<String> response = HttpClient.send(port, "/register", "account=asdf&password=asdf&email=abc@abc.com",
            ContentType.of(MediaType.APPLICATION_X_WWW_FORM_URL_ENCODED.value));

        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(302),
            () -> assertThat(response.headers().firstValue(HeaderKey.LOCATION.value)).isPresent()
                                                                                     .hasValue("/index.html")
        );
    }
}
