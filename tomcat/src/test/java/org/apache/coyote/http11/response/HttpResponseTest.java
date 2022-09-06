package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @DisplayName("바이트 배열 타입으로 http response를 반환한다.")
    @Test
    void getBytes() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        HttpResponse response = HttpResponse.status(HttpStatus.OK)
                .setHeader("Content-Type", ContentType.HTML.value())
                .setHeader("Content-Length", body.length())
                .body(body);
        byte[] actual = response.getBytes();
        byte[] expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html ",
                "Content-Length: 5518 ",
                "",
                body).getBytes();

        // then
        assertThat(actual).isEqualTo(expected);

    }
}
