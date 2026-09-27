package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void 쿼리_파라미터를_파싱한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        final HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(httpRequest)));

        // then
        assertThat(request.getQueryParameter("account")).isEqualTo("gugu");
    }

    @Test
    void 바디_파라미터를_파싱한다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body);

        // when
        final HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(httpRequest)));

        // then
        assertThat(request.getBodyParameter("account")).isEqualTo("gugu");
        assertThat(request.getBodyParameter("password")).isEqualTo("password");
    }

    @Test
    void POST_요청의_쿼리_파라미터와_바디_파라미터를_각각_조회한다() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login?redirect=/mypage HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body);

        // when
        final HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(httpRequest)));

        // then
        assertThat(request.getQueryParameter("redirect")).isEqualTo("/mypage");
        assertThat(request.getBodyParameter("account")).isEqualTo("gugu");
        assertThat(request.getQueryParameter("account")).isNull();
        assertThat(request.getBodyParameter("redirect")).isNull();
    }
}
