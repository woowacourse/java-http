package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class HttpUrlTest {

    @Test
    void createTest_whenOnlyExistPath() {
        String url = "/login";
        HttpUrl expected = new HttpUrl("/login", Collections.EMPTY_MAP);

        HttpUrl actual = new HttpUrl(url);

        assertThat(actual).isEqualTo(expected);
    }


    @Test
    void createTest_whenExistQueryParameter() {
        String url = "/login?account=gugu&password=password";
        String path = "/login";
        Map<String, String> parameters = Map.of("account", "gugu", "password", "password");
        HttpUrl expected = new HttpUrl(path, parameters);

        HttpUrl actual = new HttpUrl(url);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getParameterTest_whenKeyExist() {
        String path = "/login";
        Map<String, String> parameters = Map.of("account", "gugu", "password", "password");
        HttpUrl url = new HttpUrl(path, parameters);

        Optional<String> actual = url.getParameter("account");

        assertThat(actual).contains("gugu");
    }


    @Test
    void getParameterTest_whenKeyNotExist() {
        String path = "/login";
        Map<String, String> parameters = Map.of("account", "gugu", "password", "password");
        HttpUrl url = new HttpUrl(path, parameters);

        Optional<String> actual = url.getParameter("none");

        assertThat(actual).isEmpty();
    }
}
