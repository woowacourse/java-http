package nextstep.org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpRequestTest {

    @Test
    @DisplayName("정적 팩토리 메서드 from은 객체를 생성한다.")
    void from() {
        // given
        final String httpHeader = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Length: 0 ",
                "Cookie: JSESSIONID=1q2w3e4r ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpHeader.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // when & then
        assertThatCode(() -> HttpRequest.from(bufferedReader))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "getPath 메서드는 http 요청 경로가 {0}이면 {1}을 반환한다.")
    @CsvSource(value = {"/,/", "/login.html,/login.html", "/login?user=rick&password=123,/login"})
    void getPath(final String path, final String expected) throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest("GET", path, "sessionId");

        // when
        final String actual = httpRequest.getPath();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private HttpRequest getHttpRequest(final String httpMethod, final String path, final String sessionId)
            throws IOException {
        final String httpHeader = String.join("\r\n",
                httpMethod + " " + path + " HTTP/1.1 ",
                "Content-Length: 0 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpHeader.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        return HttpRequest.from(bufferedReader);
    }
}
