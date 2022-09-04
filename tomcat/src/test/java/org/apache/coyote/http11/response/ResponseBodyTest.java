package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ResponseHeaders;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    @Test
    void redirect_시_빈_문자열을_받는다() {
        // given
        ResponseEntity responseEntity = ResponseEntity.body("redirect:index.html").status(HttpStatus.REDIRECT);

        // when
        ResponseBody responseBody = ResponseBody.of(responseEntity, ResponseHeaders.of(responseEntity));

        // then
        assertThat(responseBody.value()).isEmpty();
    }

    @Test
    void 파일을_읽어오고_content_length가_추가된다() throws URISyntaxException, IOException {
        // given
        String body = "nextstep.txt";
        ResponseEntity responseEntity = ResponseEntity.body(body);

        // when
        ResponseHeaders responseHeaders = ResponseHeaders.of(responseEntity);
        ResponseBody responseBody = ResponseBody.of(responseEntity, responseHeaders);
        Path path = Paths.get(getClass().getClassLoader().getResource("static/nextstep.txt").toURI());
        String expectedBody = new String(Files.readAllBytes(path));
        String expectedHeaders = "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: " + "nextstep".getBytes().length + " ";

        // then
        assertAll(
                () -> assertThat(responseBody.value()).isEqualTo(expectedBody),
                () -> assertThat(responseHeaders.asString()).isEqualTo(expectedHeaders)
        );
    }
}
