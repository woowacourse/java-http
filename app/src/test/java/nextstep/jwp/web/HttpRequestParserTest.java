package nextstep.jwp.web;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestParserTest {
    @Test
    void parse() throws IOException {
        String request = "POST /register HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 58\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Accept: */*\n" +
                "\n" +
                "account=gugu&password=password&email=hkkang%40woowahan.com";

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(request.getBytes());

        HttpRequest actual = HttpRequestParser.parse(byteArrayInputStream);

        assertThat(actual.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(actual.getRequestUri().toString()).isEqualTo("/register");
        assertThat(actual.getHeaders()).containsKeys(Arrays.array(
                "Host",
                "Connection",
                "Content-Length",
                "Content-Type",
                "Accept"));
        assertThat(actual.getRequestBody()).isNotEmpty();
        assertThat(actual.getRequestBody()).isEqualTo("account=gugu&password=password&email=hkkang%40woowahan.com");
    }
}
