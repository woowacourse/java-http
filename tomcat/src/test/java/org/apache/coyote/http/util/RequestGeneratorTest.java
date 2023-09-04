package org.apache.coyote.http.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http.request.Request;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestGeneratorTest {

    @Test
    void generate_메서드는_bufferedReader를_전달하면_Request를_반환한다() throws IOException {
        final String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: text/html/charset=utf-8 ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes(StandardCharsets.UTF_8));
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final RequestGenerator generator = new RequestGenerator();

        final Request actual = generator.generate(bufferedReader);

        assertThat(actual).isNotNull();
    }

    @Test
    void generate_메서드는_url로_query_string이_있는_bufferedReader를_전달하면_Request를_반환한다() throws IOException {
        final String requestMessage = String.join("\r\n",
                "GET /login?user=gugu HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: text/html/charset=utf-8 ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes(StandardCharsets.UTF_8));
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final RequestGenerator generator = new RequestGenerator();

        final Request actual = generator.generate(bufferedReader);

        assertThat(actual.findQueryParameterValue("user")).isEqualTo("gugu");
    }

    @Test
    void generate_메서드는_body로_query_string이_있는_bufferedReader를_전달하면_Request를_반환한다() throws IOException {
        final String requestMessage = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: text/html/charset=utf-8 ",
                "Content-Length: 9 ",
                "",
                "user=gugu");
        final InputStream inputStream = new ByteArrayInputStream(requestMessage.getBytes(StandardCharsets.UTF_8));
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final RequestGenerator generator = new RequestGenerator();

        final Request actual = generator.generate(bufferedReader);

        assertThat(actual.findQueryParameterValue("user")).isEqualTo("gugu");
    }
}
