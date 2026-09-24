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

        assertThat(request.getBodyParameter("account")).isEqualTo("kang rae");
        assertThat(request.getBodyParameter("password")).isEqualTo("secret");
        assertThat(request.getBodyParameter("email")).isEqualTo("kang@example.com");
    }

    @Test
    void query_and_body_parameters_are_managed_separately() {
        final HttpRequest request = new HttpRequest(
                new RequestLine("POST /login?account=query+user HTTP/1.1"),
                new Headers(),
                new RequestBody("account=body+user&password=secret")
        );

        assertThat(request.getQueryParameter("account")).isEqualTo("query user");
        assertThat(request.getBodyParameter("account")).isEqualTo("body user");
        assertThat(request.getBodyParameter("password")).isEqualTo("secret");
    }

}
