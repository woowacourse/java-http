package nextstep;

import static nextstep.jwp.webserver.response.ContentType.HTML;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.webserver.response.StatusCode;
import nextstep.mockweb.request.MockRequest;
import nextstep.mockweb.result.MockResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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
        assertThat(result.statusCode()).isEqualTo(StatusCode.OK);
        assertThat(result.headerValue("Content-Type")).isEqualTo(HTML.contentType());
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
