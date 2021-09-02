package nextstep.jwp.web.http.request;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HTTP Request를 파싱하여 저장하는 로직을 테스트한다.")
class HttpRequestTest {

    @DisplayName("GET 메소드를 잘 가지고오는지 테스트한다. ")
    @Test
    void getMethod_Get() throws IOException {

        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.getIndexHtml().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("POST 메소드를 잘 가지고오는지 테스트한다. ")
    @Test
    void getMethod_Post() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.postRegisterUser().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
    }

    @DisplayName("/login url을 잘 가져오는지 테스트한다.")
    @Test
    void getUrl_loginPage() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.getFirstLogin().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getUrl()).isEqualTo("/login");
    }

    @DisplayName("401에러 페이지 url을 잘 가져오는지 테스트한다.")
    @Test
    void getUrl_error401Page() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.getError401Html().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getUrl()).isEqualTo("/401.html");
    }

    @DisplayName("Request Body에 attribute가 있는 경우 해당 값을 가져온다 - 로그인")
    @Test
    void getAttribute_postLogin() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.postRegisterUser().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getAttribute("account")).isEqualTo("newUser");
        assertThat(request.getAttribute("password")).isEqualTo("password");
    }

    @DisplayName("Request Body에 attribute가 있는 경우 해당 값을 가져온다 - 회원가입")
    @Test
    void getAttribute_postRegister() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.postRegisterUser().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getAttribute("account")).isEqualTo("newUser");
        assertThat(request.getAttribute("password")).isEqualTo("password");
        assertThat(request.getAttribute("email")).isEqualTo("hkkang%40woowahan.com");
    }

    @DisplayName("Cookie 헤더가 있는 요청의 쿠키값을 가져온다 - 성공")
    @Test
    void getCookieValue_Success() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.getFirstLoginWithCookie().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getCookieValue("yummy-cookie")).isEqualTo("choco");
    }

    @DisplayName("요청의 세션을 가져온다 - 성공")
    @Test
    void getSession_Success() throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(HttpRequestFactory.getFirstLogin().getBytes());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest(bufferedReader);

        assertThat(request.getSession()).isNotNull();
    }
}
