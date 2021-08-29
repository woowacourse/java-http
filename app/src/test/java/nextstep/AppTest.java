package nextstep;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.core.mvc.FrontHandler;
import nextstep.jwp.webserver.response.StatusCode;
import nextstep.mockweb.request.MockRequest;
import nextstep.mockweb.result.MockResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AppTest {

    @DisplayName("페이지 테스트")
    @ParameterizedTest
    @CsvSource(value = {"index.html:/", "login.html:/login", "register.html:/register",
            "401.html:/unauthorized"},
            delimiter = ':')
    public void pageTest(String page, String api) throws Exception {
        // when
        final MockResult result = MockRequest.get(api).result();

        // then
        페이지_비교(result.body(), page);
        Assertions.assertThat(result.statusCode()).isEqualTo(StatusCode.OK);
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    public void login_success() throws Exception{
        final MockResult result =
                MockRequest.get("/login?account=nabom&password=nabom")
                        .result();

        Assertions.assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        Assertions.assertThat(result.headerValue("Location")).isEqualTo("/");
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void login_fail() throws Exception{
        final MockResult result =
                MockRequest.get("/login?account=nabom&password=nabom12")
                        .result();

        Assertions.assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        Assertions.assertThat(result.headerValue("Location")).isEqualTo("/unauthorized");
    }

    private void 페이지_비교(String body, String path) {
        Assertions.assertThat(body).containsIgnoringWhitespaces(getPage(path));
    }

    private String getPage(String path) {
        try {
            final URL url = getClass().getClassLoader().getResource("static/" + path);
            byte[] body = Files.readAllBytes(new File(url.toURI()).toPath());
            return new String(body);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
