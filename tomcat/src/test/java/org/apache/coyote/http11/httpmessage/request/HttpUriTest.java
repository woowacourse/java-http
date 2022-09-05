package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class HttpUriTest {

    @Test
    void uri에서_path를_분리할_수_있다() {
        // given
        String uri = "/index.html";

        // when
        HttpUri httpUri = HttpUri.of(uri);

        // then
        assertThat(httpUri).extracting("path", "queryParams")
                .containsExactly("/index.html", new HashMap<>());
    }

    @Test
    void uri에서_path와_parameter를_분리할_수_있다() {
        // given
        String uri = "/login?account=gugu&password=password";

        // when
        HttpUri httpUri = HttpUri.of(uri);

        // then
        assertThat(httpUri).extracting("path", "queryParams")
                .contains("/login", Map.of("account", "gugu", "password", "password"));
    }

    @Test
    void 잘못된_uri_형식이_입력된_경우_예외를_던진다() {
        // given
        String uri = "login?account=gugu&password=password";

        // when & then
        assertThatThrownBy(() -> HttpUri.of(uri))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 전달받은_pattern이_uri와_일치하는지_확인한다() {
        // given
        String uri = "/index.html";
        HttpUri httpUri = HttpUri.of(uri);
        Pattern pattern = Pattern.compile(uri);

        // when
        boolean result = httpUri.match(pattern);

        // then
        assertThat(result).isTrue();
    }


    @Test
    void 전달받은_pattern이_uri와_일치하지_않는지_확인한다() {
        // given
        String uri = "/index.html";
        HttpUri httpUri = HttpUri.of(uri);
        Pattern pattern = Pattern.compile("/login.html");

        // when
        boolean result = httpUri.match(pattern);

        // then
        assertThat(result).isFalse();
    }
}
