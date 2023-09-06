package org.apache.coyote.http11;

import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookiesTest {

    @Test
    void getCookie() {
        //given
        final var cookies = new Cookies(Map.of("key", "value"));

        //when
        final var actual = cookies.getCookie("key");

        //then
        assertThat(actual).isEqualTo("value");
    }

}
