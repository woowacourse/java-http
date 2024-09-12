package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("body가 있는 http response 메시지를 반환한다.")
    @Test
    void convertMessageWithBody() {
        HttpResponse response = new HttpResponse(
                "HTTP/1.1",
                HttpStatusCode.OK,
                List.of("Content-Type: text/html;charset=utf-8"),
                "Hello world!"
        );

        String actual = response.convertMessage();

        assertThat(actual).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "Hello world!"
        );
    }

    @DisplayName("body가 없는 http response 메시지를 반환한다.")
    @Test
    void convertMessageWithNoBody() {
        HttpResponse response = new HttpResponse(
                "HTTP/1.1",
                HttpStatusCode.OK,
                List.of("Content-Type: text/html;charset=utf-8")
        );

        String actual = response.convertMessage();

        assertAll(
                () -> assertThat(actual).contains("Content-Length: 0"),
                () -> assertThat(actual).doesNotContain("Hello world!")
        );
    }
}
