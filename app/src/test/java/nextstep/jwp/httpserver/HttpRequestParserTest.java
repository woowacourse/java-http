package nextstep.jwp.httpserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.httpserver.domain.request.HttpMethod;
import nextstep.jwp.httpserver.domain.HttpVersion;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("HttpRequestParser 테스트")
class HttpRequestParserTest {

    @Test
    @DisplayName("HttpRequest 파싱하기")
    void parse() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        HttpRequest request = HttpRequestParser.parse(inputStream);

        // then
        assertThat(request.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getRequestUri()).isEqualTo("/index.html");
        assertThat(request.getRequestLine().getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        assertTrue(request.getHeaders().getHeaders().containsKey("Host"));
        assertTrue(request.getHeaders().getHeaders().containsKey("Connection"));
    }
}
