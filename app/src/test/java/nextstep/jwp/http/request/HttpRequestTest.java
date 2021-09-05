package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {
    private HttpRequest getRequest;
    private HttpRequest postRequest;

    @BeforeEach
    private void setup() throws IOException {
        InputStream getIn = new FileInputStream("/" + getClass().getClassLoader().getResource("get.txt").getFile());
        getRequest = HttpRequest.of(getIn);

        InputStream postIn = new FileInputStream("/" + getClass().getClassLoader().getResource("post.txt").getFile());
        postRequest = HttpRequest.of(postIn);
    }

    @DisplayName("쿠기값을 얻어온다.")
    @Test
    void getCookies() {
        HttpCookie httpCookie = getRequest.getCookies();
        assertThat(httpCookie.getCookie("yummy_cookie")).isEqualTo("choco");
        assertThat(httpCookie.getCookie("tasty_cookie")).isEqualTo("strawberry");
    }

    @DisplayName("HTTP 메소드 값을 얻어온다.")
    @Test
    void checkMethod() {
        assertThat(getRequest.checkMethod("GET")).isTrue();
    }

    @DisplayName("Body의 쿼리 파라미터 값을 얻어온다.")
    @Test
    void getQueryValue() {
        assertThat(postRequest.getQueryValue("userId")).isEqualTo("test");
        assertThat(postRequest.getQueryValue("password")).isEqualTo("test");
    }

    @DisplayName("요청경로를 얻어온다.")
    @Test
    void getPath() {
        String path = getRequest.getPath();
        assertThat(path).isEqualTo("/login");
    }

    @DisplayName("세션을 얻어온다.")
    @Test
    void getSession() {
        HttpCookie httpCookie = getRequest.getCookies();
        HttpSession httpSession = getRequest.getSession().get();
        assertThat(httpSession.getId()).isEqualTo(httpCookie.getCookie("JSESSIONID"));
    }
}