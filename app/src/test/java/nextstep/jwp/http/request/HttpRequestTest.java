package nextstep.jwp.http.request;

import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.session.HttpSession;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    void getCookies() {
        HttpCookie httpCookie = getRequest.getCookies();
        assertThat(httpCookie.getCookie("yummy_cookie")).isEqualTo("choco");
        assertThat(httpCookie.getCookie("tasty_cookie")).isEqualTo("strawberry");
    }

    @Test
    void checkMethod() {
        assertThat(getRequest.checkMethod("GET")).isTrue();
    }

    @Test
    void getQueryValue() {
        assertThat(postRequest.getQueryValue("userId")).isEqualTo("test");
        assertThat(postRequest.getQueryValue("password")).isEqualTo("test");
    }

    @Test
    void getPath() {
        String path = getRequest.getPath();
        assertThat(path).isEqualTo("/login");
    }

    @Test
    void getSession() {
        HttpCookie httpCookie = getRequest.getCookies();
        HttpSession httpSession = getRequest.getSession();
        assertThat(httpSession.getId()).isEqualTo(httpCookie.getCookie("JSESSIONID"));
    }
}