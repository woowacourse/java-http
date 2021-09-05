package nextstep.jwp.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.constants.Header;
import nextstep.jwp.constants.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpTermsResponseTest {

    @Test
    @DisplayName("기본설정과 statusCode 응답을 확인한다.")
    void buildStatusCode() {
        String actual = HttpResponse
                .statusCode(StatusCode.FOUND)
                .build();

        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("추가 헤더 설정 응답을 확인한다.")
    void buildAddHeaders() {
        String actual = HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .build();

        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "Location: /index.html ",
                "",
                "");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("추가 문자열 바디 설정 응답을 확인한다.")
    void buildBody() {
        String actual = HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .responseBody("hello")
                .build();

        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5 ",
                "Location: /index.html ",
                "",
                "hello");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("추가 리소스 바디 설정 응답을 확인한다.")
    void buildResourceBody() throws IOException {
        String actual = HttpResponse
                .statusCode(StatusCode.FOUND)
                .addHeaders(Header.LOCATION, "/index.html")
                .responseResource("/index.html")
                .build();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ",
                "Location: /index.html ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(actual).isEqualTo(expected);
    }
}
