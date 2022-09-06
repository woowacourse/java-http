package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import nextstep.jwp.exception.NotFoundException;
import org.apache.coyote.http11.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UrlTest {

    @DisplayName("기본 페이지에 대한 응답을 반환한다.")
    @Test
    void getResponse_default() {
        final Http11Response response = Url.getResponseFrom("/");

        final String actual = response.getOkResponse();

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지에 대한 응답을 반환한다")
    @Test
    void getResponse_login() throws IOException {
        final Http11Response response = Url.getResponseFrom("/login?account=gugu&password=password");

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

    @DisplayName("인덱스 페이지에 대한 응답을 반환한다")
    @Test
    void getResponse_index() throws IOException {
        final Http11Response response = Url.getResponseFrom("/index.html");

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

    @DisplayName(".css 파일에 대한 응답을 반환한다")
    @Test
    void getResponse_css() throws IOException {
        final Http11Response response = Url.getResponseFrom("/css/styles.css");

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
        assertThatThrownBy(() -> Url.getResponseFrom("야호"))
                .isInstanceOf(NotFoundException.class);
    }
}
