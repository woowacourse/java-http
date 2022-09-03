package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpUriTest {

    @Test
    void uri에서_path를_분리할_수_있다() {
        // given
        String uri = "/index.html";

        // when
        HttpUri httpUri = HttpUri.of(uri);

        // then
        assertAll(
                () -> assertThat(httpUri.getPath()).isEqualTo(uri),
                () -> assertThat(httpUri.getQueryParams()).isEmpty()
        );
    }

    @Test
    void uri에서_path와_parameter를_분리할_수_있다() {
        // given
        String uri = "/login?account=gugu&password=password";

        // when
        HttpUri httpUri = HttpUri.of(uri);

        // then
        assertAll(
                () -> assertThat(httpUri.getPath()).isEqualTo("/login"),
                () -> assertThat(httpUri.getQueryParams()).usingRecursiveComparison()
                        .isEqualTo(Map.of("account", "gugu", "password", "password"))
        );
    }

    @Test
    void 잘못된_uri_형식이_입력된_경우_예외를_던진다() {
        // given
        String uri = "login?account=gugu&password=password";

        // when & then
        assertThatThrownBy(() -> HttpUri.of(uri))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
