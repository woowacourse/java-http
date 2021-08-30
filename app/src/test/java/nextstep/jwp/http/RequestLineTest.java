package nextstep.jwp.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestLineTest {
    @Test
    void GET() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getUri()).isEqualTo("/index.html");
    }

    @Test
    void POST() {
        RequestLine requestLine = new RequestLine("POST /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(requestLine.getUri()).isEqualTo("/index.html");
    }

    @Test
    void queryString() {
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=password");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getUri()).isEqualTo("/login");
        assertThat(requestLine.getParams().size()).isEqualTo(2);
    }
}
