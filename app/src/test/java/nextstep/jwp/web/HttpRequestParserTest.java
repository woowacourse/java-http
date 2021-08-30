package nextstep.jwp.web;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestParserTest {
    @Test
    void parse() throws IOException {
        // given
        String request = "POST /register HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 58\n" +
                "Content-Type: application/x-www-form-urlencoded\n" +
                "Accept: */*\n" +
                "\n" +
                "account=gugu&password=password&email=hkkang%40woowahan.com";

        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest actual = HttpRequestParser.parse(inputStream);

        // then
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

    @Test
    void parseParameter() throws IOException {
        // given
        String request = "GET /login?account=gugu&password=password HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*\n";
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());

        // when
        HttpRequest actual = HttpRequestParser.parse(inputStream);

        // then
        assertThat(actual.getParameters()).containsAllEntriesOf(Map.of("account", "gugu", "password", "password"));
    }
}
