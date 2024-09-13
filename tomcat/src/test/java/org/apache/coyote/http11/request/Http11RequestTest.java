package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestTest {

    @DisplayName("Http Headers를 추출한다.")
    @Test
    void createHttpHeaders() throws IOException {
        String httpRequest = String.join(
                "\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        Http11Request http11Request = Http11Request.from(inputStream);
        Http11RequestHeader headers = http11Request.getHeaders();

        assertThat(headers.getHeaders())
                .containsExactlyInAnyOrderEntriesOf(Map.of(
                        "Host", List.of("localhost:8080"),
                        "Accept", List.of("text/css,*/*;q=0.1"),
                        "Connection", List.of("keep-alive")
                ));
    }

    @DisplayName("Http Body를 추출한다.")
    @Test
    void createBody() throws IOException {
        String httpRequest = String.join(
                "\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Content-Length: 30",
                "Connection: keep-alive ",
                "",
                "account=gugu&password=password"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        Http11Request http11Request = Http11Request.from(inputStream);
        String body = http11Request.getBodyValue();

        assertThat(body).isEqualTo("account=gugu&password=password");
    }
}
