package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpResponseTest {

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

        // when
        String actual = HttpResponse
                .builder()
                .statusCode(HttpStatusCode.OK)
                .responseBody(body)
                .build();

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
        // when
        String actual = HttpResponse.builder()
                .statusCode(HttpStatusCode.OK)
                .staticResource(path)
                .build();

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

        // when
        String actual = HttpResponse
                .builder()
                .statusCode(HttpStatusCode.FOUND)
                .redirect("index.html")
                .build();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}