package nextstep.org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    @DisplayName("body에 String이 들어올 경우 responseBody는 문자열이 된다.")
    void stringBody() {
        // given
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 12",
                "Content-Type: text/html;charset=utf-8",
                "",
                "Hello world!");

        // when
        final HttpResponse response = new HttpResponse();
        response.setBody("Hello world!");
        response.setStatus(HttpStatus.OK);

        final String actual = response.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("body에 url이 들어올 경우 responseBody는 정적 리소스가 된다.")
    void stringUrl() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        Objects.requireNonNull(resource);

        // when
        final HttpResponse response = new HttpResponse();
        response.setBody(resource);
        response.setStatus(HttpStatus.OK);

        // then
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 5564",
                "Content-Type: text/html;charset=utf-8",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );

        final String actual = response.toString();

        assertThat(actual).isEqualTo(expected);
    }
}
