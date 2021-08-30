package nextstep.joanne.http.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class URITest {

    @Test
    void equalsWith() {
        URI uri = URI.of("/index.html");
        assertAll(
                () -> assertThat(uri.equalsWith("/index.html")).isTrue(),
                () -> assertThat(uri.equalsWith("/")).isFalse()
        );
    }

    @Test
    void equalsWithWhenURIContainsQueryString() {
        URI uri = URI.of("/login?account=joanne&password=password");
        assertAll(
                () -> assertThat(uri.equalsWith("/login.html")).isTrue(),
                () -> assertThat(uri.equalsWith("/")).isFalse()
        );
    }

    @Test
    void contains() {
        URI uri = URI.of("/index.html");
        assertAll(
                () -> assertThat(uri.contains("index")).isTrue(),
                () -> assertThat(uri.contains("register")).isFalse()
        );
    }

    @Test
    void containsWhenURIContainsQueryString() {
        URI uri = URI.of("/login?account=joanne&password=password");
        assertAll(
                () -> assertThat(uri.contains("login")).isTrue(),
                () -> assertThat(uri.contains("register")).isFalse()
        );
    }
}