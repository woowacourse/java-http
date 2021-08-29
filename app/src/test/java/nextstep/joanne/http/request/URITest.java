package nextstep.joanne.http.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class URITest {

    @Test
    void equalsWith() {
        URI uri = new URI("/index.html");
        assertAll(
                () -> assertThat(uri.equalsWith("/index.html")).isTrue(),
                () -> assertThat(uri.equalsWith("/")).isFalse()
        );
    }

    @Test
    void equalsWithWhenURIContainsQueryString() {
        URI uri = new URI("/login?account=joanne&password=password");
        assertAll(
                () -> assertThat(uri.equalsWith("/login")).isTrue(),
                () -> assertThat(uri.equalsWith("/")).isFalse()
        );
    }

    @Test
    void contains() {
        URI uri = new URI("/index.html");
        assertAll(
                () -> assertThat(uri.contains("index")).isTrue(),
                () -> assertThat(uri.contains("register")).isFalse()
        );
    }

    @Test
    void containsWhenURIContainsQueryString() {
        URI uri = new URI("/login?account=joanne&password=password");
        assertAll(
                () -> assertThat(uri.contains("login")).isTrue(),
                () -> assertThat(uri.contains("register")).isFalse()
        );
    }
}