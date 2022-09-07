package nextstep.org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("정적 팩토리 메서드 init은 객체를 생성한다.")
    void init() {
        // when & then
        assertThatCode(() -> HttpResponse.init(HttpStatusCode.OK))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("setBody 메서드는 문자열 response body를 저장한다.")
    void setBdy() {
        // given
        final String responseBody = "Hello Wooteco";
        final HttpResponse httpResponse = HttpResponse.init(HttpStatusCode.OK);

        // when
        httpResponse.setBody(responseBody);

        // then
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        assertThat(actual).contains(responseBody);
    }

    @Test
    @DisplayName("setLocationAsHome 메서드는 http 응답에 Location이 /index.html인 헤더를 추가한다.")
    void setLocationAsHome() {
        // given
        final HttpResponse httpResponse = HttpResponse.init(HttpStatusCode.OK);

        // when
        httpResponse.setLocationAsHome();

        // then
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        assertThat(actual).contains("Location: /index.html");
    }

    @Test
    @DisplayName("addCookie 메서드는 http 응답 Set-Cookie헤더에 JSESSIONID를 추가한다.")
    void addCookie() {
        // given
        final String sessionId = "1q2w3e4r";
        final HttpResponse httpResponse = HttpResponse.init(HttpStatusCode.OK);

        // when
        httpResponse.addCookie("JSESSIONID", sessionId);

        // then
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        assertThat(actual).contains("Set-Cookie: JSESSIONID=" + sessionId);
    }

    @Test
    @DisplayName("toResponseBytes 메서드는 http 응답을 byte 배열로 반환한다.")
    void toResponseBytes() {
        // given
        final String responseBody = "Hello world!";
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);

        final HttpResponse httpResponse = HttpResponse.init(HttpStatusCode.OK)
                .setBody(responseBody);

        // when
        final byte[] response = httpResponse.toResponseBytes();
        final String actual = new String(response);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Nested
    @DisplayName("setBodyByPath 메서드는")
    class SetBodyByPath {

        @Test
        @DisplayName("path에 해당하는 리소스가 존재하면 response body에 해당 리소스를 읽어와서 저장한다.")
        void setBodyByPath_existResource_saveResponseBody() throws IOException {
            // given
            final HttpResponse httpResponse = HttpResponse.init(HttpStatusCode.OK);

            // when
            httpResponse.setBodyByPath("/index.html");

            // then
            final byte[] response = httpResponse.toResponseBytes();
            final String actual = new String(response);

            final String expected = readFile(HttpStatusCode.OK, "index.html");

            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("path에 해당하는 리소스가 존재하지 않으면 response body에 404.html 파일을 읽어와서 저장한다.")
        void setBodyByPath_notExistResource_save404() throws IOException {
            // given
            final HttpResponse httpResponse = HttpResponse.init(HttpStatusCode.OK);

            // when
            httpResponse.setBodyByPath("/not-exist-resource");

            // then
            final byte[] response = httpResponse.toResponseBytes();
            final String actual = new String(response);

            final String expected = readFile(HttpStatusCode.NOT_FOUND, "404.html");

            assertThat(actual).isEqualTo(expected);
        }

        private String readFile(final HttpStatusCode statusCode, final String resourceFile) throws IOException {
            final URL resource = getClass().getClassLoader().getResource("static/" + resourceFile);
            final String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            return statusCode.getResponseStartLine() + " \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + expectedResponseBody.getBytes().length + " \r\n" +
                    "\r\n" +
                    expectedResponseBody;
        }
    }
}
