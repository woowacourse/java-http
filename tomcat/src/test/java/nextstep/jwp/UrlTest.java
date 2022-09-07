package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlTest {

    @DisplayName("기본 페이지에 대한 응답을 반환한다.")
    @Test
    void getResponse_default() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지에 대한 응답을 반환한다.")
    @Test
    void getResponse_login() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("GET /login HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final java.net.URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("로그인에 성공하면 인덱스 페이지로 리다이렉트 시킨다.")
    @Test
    void getResponse_loginSuccess() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "GET /login?account=gugu&password=password HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final java.net.URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("로그인에 실패하면 401 페이지를 반환한다.")
    @Test
    void getResponse_loginFailure() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream(
                "GET /login?account=gugu&password=p HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final java.net.URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("회원가입 페이지에 대한 응답을 반환한다.")
    @Test
    void getResponse_register() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("GET /register HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final java.net.URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("인덱스 페이지에 대한 응답을 반환한다.")
    @Test
    void getResponse_index() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("GET /index.html HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final java.net.URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName(".css 파일에 대한 응답을 반환한다.")
    @Test
    void getResponse_css() throws IOException {
        final InputStream inputStream = new ByteArrayInputStream("GET /css/styles.css HTTP/1.1".getBytes());
        final Http11Response response = Url.getResponseFrom(Http11Request.of(inputStream));

        final String actual = response.getOkResponse();

        final java.net.URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 경로를 요청하면 예외를 반환한다.")
    @Test
    void getResponse_NotFoundException() {
        final InputStream inputStream = new ByteArrayInputStream("GET 야호 HTTP/1.1".getBytes());
        assertThatThrownBy(() -> Url.getResponseFrom(Http11Request.of(inputStream)))
                .isInstanceOf(NotFoundException.class);
    }
}
