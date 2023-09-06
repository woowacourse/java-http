package org.apache.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.common.Cookie;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void HttpRequest_객체를_생성한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        assertThat(httpRequest).isNotNull();
    }

    @Test
    void HTTP_GET_요청_시_isGet_메서드는_true를_반환한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        assertThat(httpRequest.isGet()).isTrue();
    }

    @Test
    void HTTP_POST_요청_시_isGet_메서드는_false를_반환한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        assertThat(httpRequest.isGet()).isFalse();
    }

    @Test
    void HTTP_POST_요청_시_isPost_메서드는_true를_반환한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        assertThat(httpRequest.isPost()).isTrue();
    }

    @Test
    void HTTP_GET_요청_시_isPost_메서드는_false를_반환한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        assertThat(httpRequest.isPost()).isFalse();
    }

    @Test
    void 쿠키_헤더가_존재하면_값을_담은_쿠키를_반환한다() throws IOException {
        String httpRequestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=1234-15125-436; CANDY=LEMON"
        );
        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequest.from(bufferedReader);
        Cookie cookie = httpRequest.getCookie();

        assertAll(
                () -> assertThat(cookie.getValue("JSESSIONID")).isEqualTo("1234-15125-436"),
                () -> assertThat(cookie.getValue("CANDY")).isEqualTo("LEMON")
        );
    }
}
