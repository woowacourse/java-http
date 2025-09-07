package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpRequestUrlTest {

    @Test
    void request_url을_파싱한다() {
        String url = "/login?account=jjangu&password=hooni";

        HttpRequestUrl httpRequestUrl = new HttpRequestUrl(url);

        String path = httpRequestUrl.getPath();
        String accountValue = httpRequestUrl.getParameter("account");
        String passwordValue = httpRequestUrl.getParameter("password");

        assertThat(path).isEqualTo("/login");
        assertThat(accountValue).isEqualTo("jjangu");
        assertThat(passwordValue).isEqualTo("hooni");
    }

}