package nextstep.jwp.http.entity;

import org.junit.jupiter.api.Test;

class HttpHeadersTest {
    @Test
    void name() {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.addHeader("Content-Length: 357");
        httpHeaders.addHeader("key: value");
        httpHeaders.addHeader("name: value");

        System.out.println(httpHeaders.asString());
    }
}
