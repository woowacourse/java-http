package nextstep.jwp.web.network;

import nextstep.jwp.web.network.response.ContentType;
import nextstep.jwp.web.network.response.HttpResponse;
import nextstep.jwp.web.network.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpResponseTest {

    @Test
    void createWithByte() {
        // given
        final byte[] bytes = "Hello World!".getBytes();

        // when // then
        assertThatCode(() -> HttpResponse.ofByteArray(HttpStatus.OK, bytes))
                .doesNotThrowAnyException();
    }

    @Test
    void createWithString() {
        // given
        final String string = "Hello World!";

        // when // then
        assertThatCode(() -> HttpResponse.ofString(HttpStatus.OK, ContentType.HTML, string))
                .doesNotThrowAnyException();
    }

    @DisplayName("응답을 String으로 변환한다 - 성공")
    @Test
    void asString() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String resourceAsString = new String(Files.readAllBytes(Paths.get(Objects.requireNonNull(resource).getPath())));

        // when
        final HttpResponse httpResponse = HttpResponse.ofString(HttpStatus.OK, ContentType.HTML, resourceAsString);
        final String actual = httpResponse.asString();

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourceAsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourceAsString;
        assertThat(actual).isEqualTo(expected);
    }
}