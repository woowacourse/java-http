package nextstep.jwp.http.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpHeadersTest {
    @Test
    void name() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String header1 = "Content-Length: 357 ";
        String header2 = "key: value ";
        String header3 = "name: value ";

        httpHeaders.addHeader(header1);
        httpHeaders.addHeader(header2);
        httpHeaders.addHeader(header3);

        assertThat(httpHeaders.asString()).containsExactly(header1, header2, header3);
    }
}
