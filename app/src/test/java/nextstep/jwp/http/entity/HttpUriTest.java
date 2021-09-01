package nextstep.jwp.http.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpUriTest {

    @Test
    void onlyPath() {
        HttpUri uri = HttpUri.of("/login");
        assertThat(uri.path()).isEqualTo("/login");
    }

    @Test
    void pathWithQuery() {
        HttpUri uri = HttpUri.of("/login?account=gugu&password=password");

        assertThat(uri.path()).isEqualTo("/login");
        assertThat(uri.queryString()).isEqualTo("account=gugu&password=password");
    }
}
