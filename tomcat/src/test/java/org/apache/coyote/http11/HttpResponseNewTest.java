package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpResponseNewTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
    }

    @AfterEach
    void cleanUp() throws IOException {
        outputStream.close();
    }

    @DisplayName("HTTP response 값을 생성한다.")
    @Test
    void build() {
        // given
        String body = "body contents";
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body
        );
        HttpResponseNew response = new HttpResponseNew(outputStream);

        // when
        response.statusCode(HttpStatusCode.OK)
                .responseBody(body);
        String actual = outputStream.toString(StandardCharsets.UTF_8);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("static 확장자 별로 content-type을 설정할 수 있다.")
    @CsvSource({
            "/index.html,text/html",
            "/js/scripts.js,text/javascript",
            "/css/styles.css,text/css",
            "/assets/img/error-404-monochrome.svg,image/svg+xml"
    })
    @ParameterizedTest
    void buildStaticResource(String path, String contentType) {
        // given
        HttpResponseNew response = new HttpResponseNew(outputStream);

        // when
        response.statusCode(HttpStatusCode.OK)
                .staticResource(path);

        String actual = outputStream.toString(StandardCharsets.UTF_8);

        // then
        assertThat(actual).contains(contentType);
    }

    @DisplayName("HTTP redirect response 값을 생성한다.")
    @Test
    void redirect() {
        // given
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: index.html ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 0 ",
                "\r\n"
        );
        HttpResponseNew response = new HttpResponseNew(outputStream);

        // when
        response.statusCode(HttpStatusCode.FOUND)
                .redirect("index.html");

        String actual = outputStream.toString(StandardCharsets.UTF_8);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("세션 id를 응답할 수 있다.")
    @Test
    void setSession() {
        // given
        HttpResponseNew response = new HttpResponseNew(outputStream);

        // when
        response.statusCode(HttpStatusCode.OK)
                .createSession("name", "value")
                .responseBody("test body");

        String actual = outputStream.toString(StandardCharsets.UTF_8);

        // then
        assertThat(actual).contains("Set-Cookie");
    }
}