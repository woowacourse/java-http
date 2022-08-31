package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.web.Request;
import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void from() {
        Request request = Request.from("GET /index.html HTTP/1.1");
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void getRequestExtension() {
        Request request = Request.from("GET /index.html HTTP/1.1");
        assertThat(request.getRequestExtension()).isEqualTo("html");
    }

    @Test
    void getDefaultRequestExtension() {
        Request request = Request.from("GET /api/login HTTP/1.1");
        assertThat(request.getRequestExtension()).isEqualTo("strings");
    }

    @Test
    void isFileRequest() {
        Request request = Request.from("GET /index.html HTTP/1.1");
        assertThat(request.isFileRequest()).isTrue();
    }

    @Test
    void isNotFileRequest() {
        Request request = Request.from("GET /api/login HTTP/1.1");
        assertThat(request.isFileRequest()).isFalse();
    }

    @Test
    void getQueryParameters() {
        Request request = Request.from("GET /login?account=gugu&password=password HTTP/1.1");
        assertThat(request.getQueryParameters()).containsKeys("account", "password");
    }

    @Test
    void getEmptyQueryParameters() {
        Request request = Request.from("GET /login HTTP/1.1");
        assertThat(request.getQueryParameters()).isEmpty();
    }

    @Test
    void isSameRequestUrl() {
        Request request = Request.from("GET /login?account=gugu&password=password HTTP/1.1");
        assertThat(request.isSameRequestUrl("/login")).isTrue();
    }
}
