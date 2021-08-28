package nextstep;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.webserver.response.StatusCode;
import nextstep.mockweb.request.MockRequest;
import nextstep.mockweb.result.MockResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AppTest {


    @Test
    @DisplayName("index 페이지")
    public void indexPage() throws Exception {
        // given
        String page = "index.html";

        // when
        final MockResult result = MockRequest.get("/").result();

        // then
        페이지_비교(result.body(), page);
        Assertions.assertThat(result.statusCode()).isEqualTo(StatusCode.OK);
    }

    @Test
    @DisplayName("로그인 페이지")
    public void loginPage() throws Exception{
        //given
        String page = "login.html";

        // when
        final MockResult result = MockRequest.get("/login").result();

        // then
        페이지_비교(result.body(), page);
        Assertions.assertThat(result.statusCode()).isEqualTo(StatusCode.OK);
    }

    @Test
    @DisplayName("회원가입 페이지")
    public void registerPage() throws Exception{
        //given
        String page = "register.html";

        // when
        final MockResult result = MockRequest.get("/register").result();

        // then
        페이지_비교(result.body(), page);
        Assertions.assertThat(result.statusCode()).isEqualTo(StatusCode.OK);
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
