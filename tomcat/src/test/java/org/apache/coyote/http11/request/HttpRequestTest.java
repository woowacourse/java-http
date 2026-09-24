package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.Headers;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void form_parameters_are_decoded() {
        final HttpRequest request = new HttpRequest(
                new RequestLine("POST /register HTTP/1.1"),
                new Headers(),
                new RequestBody("account=kang+rae&password=secret&email=kang%40example.com")
        );

        assertThat(request.getParameter("account")).isEqualTo("kang rae");
        assertThat(request.getParameter("password")).isEqualTo("secret");
        assertThat(request.getParameter("email")).isEqualTo("kang@example.com");
    }
}
