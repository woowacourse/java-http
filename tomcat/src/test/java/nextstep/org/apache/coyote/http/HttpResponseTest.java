package nextstep.org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("정적 팩토리 메서드 from은 객체를 생성한다.")
    void from() throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest("1q2w3e4r");

        // when & then
        assertThatCode(() -> HttpResponse.from(httpRequest))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("changeStatusCode 메서드는 http 응답의 상태코드를 변경한다.")
    void changeStatusCode() throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest("1q2w3e4r");
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        // when
        httpResponse.changeStatusCode(HttpStatusCode.FOUND);

        // then
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        assertThat(actual).contains("302 Found");
    }

    @Test
    @DisplayName("setLocationAsHome 메서드는 http 응답에 Location이 /index.html인 헤더를 추가한다.")
    void setLocationAsHome() throws IOException {
        // given
        final HttpRequest httpRequest = getHttpRequest("1q2w3e4r");
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        // when
        httpResponse.setLocationAsHome();

        // then
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        assertThat(actual).contains("Location: /index.html");
    }

    @Test
    @DisplayName("setSessionId 메서드는 http 응답 Set-Cookie헤더에 JSESSIONID를 추가한다.")
    void setSessionId() throws IOException {
        // given
        final String sessionId = "1q2w3e4r";
        final HttpRequest httpRequest = getHttpRequest(sessionId);
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        // when
        httpResponse.setSessionId(sessionId);

        // then
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        assertThat(actual).contains("Set-Cookie: JSESSIONID=" + sessionId);
    }

    @Test
    @DisplayName("toResponseBytes 메서드는 http 응답을 byte 배열로 반환한다.")
    void toResponseBytes() throws IOException {
        // given
        final String responseBody = "Hello world!";
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        final HttpRequest httpRequest = getHttpRequest("1q2w3e4r");
        final HttpResponse httpResponse = HttpResponse.from(httpRequest);

        // when
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    private HttpRequest getHttpRequest(final String sessionId)
            throws IOException {
        final String startLine = "GET / HTTP/1.1 ";
        final String httpHeader = String.join("\r\n",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");

        final InputStream inputStream = new ByteArrayInputStream(httpHeader.getBytes());
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        final HttpHeader header = HttpHeader.from(bufferedReader);

        return HttpRequest.of(startLine, header);
    }
}
