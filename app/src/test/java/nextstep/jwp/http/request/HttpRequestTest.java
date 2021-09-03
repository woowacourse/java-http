package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("HTTP Request")
class HttpRequestTest {

    @DisplayName("BufferedReader를 사용하여 GET_HTTP_REQUEST 객체를 생성한다")
    @Test
    void createGet() {
        String requestAsString = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "");

        InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        HttpRequest actualRequest = new HttpRequest(bufferedReader);

        assertAll(
                () -> assertThat(actualRequest.isGet()).isTrue(),
                () -> assertThat(actualRequest.getPath()).isEqualTo("/index.html")
        );
    }

    @DisplayName("BufferedReader를 사용하여 POST_HTTP_REQUEST 객체를 생성한다")
    @Test
    void createPost() {
        String requestAsString = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 56",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang@woowahan.com");

        InputStream inputStream = new ByteArrayInputStream(requestAsString.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        HttpRequest actualRequest = new HttpRequest(bufferedReader);

        assertAll(
                () -> assertThat(actualRequest.isPost()).isTrue(),
                () -> assertThat(actualRequest.getPath()).isEqualTo("/register"),
                () -> assertThat(actualRequest.getParameter("account")).isEqualTo("gugu"),
                () -> assertThat(actualRequest.getParameter("password")).isEqualTo("password"),
                () -> assertThat(actualRequest.getParameter("email")).isEqualTo("hkkang@woowahan.com")
        );
    }
}
