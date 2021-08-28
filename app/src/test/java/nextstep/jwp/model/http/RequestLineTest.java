package nextstep.jwp.model.http;

import org.junit.jupiter.api.Test;

import static nextstep.jwp.model.http.HTTPMethod.GET;
import static nextstep.jwp.model.http.HTTPMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @Test
    void createGetMethod() {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        assertThat(requestLine.getMethod()).isEqualTo(GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
    }

    @Test
    void createPostMethod() {
        RequestLine requestLine = new RequestLine("POST /index.html HTTP/1.1");
        assertThat(requestLine.getMethod()).isEqualTo(POST);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
    }

    @Test
    void createGetMethodParams() {
        RequestLine requestLine = new RequestLine("GET /index.html?account=gugu&password=password HTTP/1.1");
        assertThat(requestLine.getMethod()).isEqualTo(GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getParams()).hasSize(2);
    }
}