package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void generateHttpRequest() {
        // given
        final List<String> request = List.of(
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        // when
        final HttpRequest httpRequest = HttpRequest.of(request);

        // then
        assertThat(httpRequest.getRequestUri().getUri()).isEqualTo("/login?account=gugu&password=password");
    }

    @Test
    void findContentTypeOfDefault() {
        // given
        final List<String> request = List.of(
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        // when
        final HttpRequest httpRequest = HttpRequest.of(request);

        // then
        assertThat(httpRequest.findContentType()).isEqualTo("text/html");
    }

    @Test
    void findContentTypeOfNotDefault() {
        // given
        final List<String> request = List.of(
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive");

        // when
        final HttpRequest httpRequest = HttpRequest.of(request);

        // then
        assertThat(httpRequest.findContentType()).isEqualTo("text/css");
    }
}
