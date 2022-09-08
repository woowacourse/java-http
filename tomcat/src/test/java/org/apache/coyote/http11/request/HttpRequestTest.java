package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void createRequestByStream() {
        // given
        String message = String.join("\n",
                List.of(
                        "GET /hello?a=1&b=2 HTTP/1.1",
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.87 Whale/3.16.138.22 Safari/537.36",
                        "",
                        "request body"
                )
        );

        InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        // when
        HttpRequest request = HttpRequest.parse(inputStream);
        // then
        assertThat(request).isNotNull();
    }

    @Test
    void createRequestWithJSessionId() {
        // given
        String message = String.join("\n",
                List.of(
                        "GET /hello?a=1&b=2 HTTP/1.1",
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.5112.87 Whale/3.16.138.22 Safari/537.36",
                        "Cookie: JSESSIONID=helloworld",
                        "",
                        "request body"
                )
        );
        InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        // when
        HttpRequest request = HttpRequest.parse(inputStream);
        // then
        assertThat(request).isNotNull();
    }

    @Test
    void ss() {
        // given

        // when

        // then
    }
}
