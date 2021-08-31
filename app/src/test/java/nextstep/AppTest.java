package nextstep;

import static nextstep.jwp.webserver.response.ContentType.HTML;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import nextstep.jwp.webserver.response.StatusCode;
import nextstep.mockweb.option.MockOption;
import nextstep.mockweb.request.MockRequest;
import nextstep.mockweb.result.MockResult;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class AppTest {

    @BeforeEach
    void setUp() {
        MockOption.setSessionId(UUID.randomUUID().toString());
    }

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

    @Test
    @DisplayName("페이지를 찾을 수 없을 때")
    public void notFoundPage() throws Exception{
        // when
        final MockResult result = MockRequest.get("/awhakschd").result();

        // then
        페이지_비교(result.body(), "404.html");
        assertThat(result.statusCode()).isEqualTo(StatusCode.NOT_FOUND);
    }

    @Test
    @DisplayName("로그인 실패")
    public void login_fail() throws Exception{
        final MockResult result = MockRequest.post("/login", null)
                .addFormData("account", "nabom")
                .addFormData("password", "nanana")
                .logAll()
                .result();

        assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        assertThat(result.headerValue("Location")).isEqualTo("/unauthorized");
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_success() throws Exception{
        final MockResult result = MockRequest.post("/login")
                .addFormData("account", "nabom")
                .addFormData("password", "nabom")
                .logAll()
                .result();

        assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        assertThat(result.headerValue("Location")).isEqualTo("/");
    }

    @Test
    @DisplayName("로그인 성공 이후 세션확인")
    public void login_success_session() throws Exception{
        MockRequest.post("/login")
                .addFormData("account", "nabom")
                .addFormData("password", "nabom")
                .logAll()
                .result();

        final MockResult result = MockRequest.get("/login")
                .logAll()
                .result();

        assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        assertThat(result.headerValue("Location")).isEqualTo("/");
    }

    @Test
    @DisplayName("회원가입 성공")
    public void register_success() throws Exception{
        final MockResult result = MockRequest.post("/register")
                .addFormData("account", "newMember")
                .addFormData("password", "newMember")
                .addFormData("email", "newMember")
                .result();

        assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        assertThat(result.headerValue("Location")).isEqualTo("/");
    }

    @Test
    @DisplayName("회원가입 실패 (중복된 회원)")
    public void register_fail() throws Exception{
        final MockResult result = MockRequest.post("/register")
                .addFormData("account", "nabom")
                .addFormData("password", "newMember")
                .addFormData("email", "newMember")
                .result();

        assertThat(result.statusCode()).isEqualTo(StatusCode.FOUND);
        assertThat(result.headerValue("Location")).isEqualTo("/register");
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
