package org.apache.coyote.http11.request.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpRequestStartLineTest {

    @Test
    @DisplayName("같은 http method인지 확인한다.")
    void isEqualToMethod() {
        HttpRequestLine request = new HttpRequestLine("GET", "/index.html", "HTTP/1.1");

        assertThat(request.isEqualToMethod(HttpMethod.GET)).isTrue();
    }

    @Test
    @DisplayName("같은 url request인지 확인한다.")
    void isEqualToUri() {
        HttpRequestLine request = new HttpRequestLine("GET", "/", "HTTP/1.1");

        assertThat(request.isEqualToUri("/")).isTrue();
    }

    @Test
    @DisplayName("쿼리요청인지 확인한다.")
    void isQuery() {
        HttpRequestLine request = new HttpRequestLine("GET", "/login?account=gugu&password=1234", "HTTP/1.1");

        assertThat(request.isQueryString()).isTrue();
    }
}
