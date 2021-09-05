package nextstep.jwp.framework.message.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookiesTest {

    @DisplayName("HttpCookies 를 생성한다.")
    @Test
    void create() {
        // given
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("choco", "cookie");
        params.put("mint", "toothpaste");
        params.put("rice", "cake");

        // when
        HttpCookies httpCookies = new HttpCookies(params);

        // then
        assertThat(httpCookies.toMap()).isEqualTo(params);
    }

    @DisplayName("문자열로 HttpCookies 를 생성한다.")
    @Test
    void createWithString() {
        // given
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("choco", "cookie");
        params.put("mint", "toothpaste");
        params.put("rice", "cake");

        // when
        HttpCookies httpCookies = HttpCookies.from("choco=cookie; mint=toothpaste; rice=cake");

        // then
        assertThat(httpCookies.toMap()).isEqualTo(params);
    }

    @DisplayName("쿠키에서 키로 값을 가져온다.")
    @Test
    void take() {
        // given
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("choco", "cookie");
        HttpCookies httpCookies = new HttpCookies(params);

        // when, then
        assertThat(httpCookies.take("choco")).get().isEqualTo("cookie");
        assertThat(httpCookies.take("bobo")).isEmpty();
    }

    @DisplayName("equals 와 hashCode 검증")
    @Test
    void equalsAndHashCode() {
        // given
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("choco", "cookie");
        params.put("mint", "toothpaste");
        params.put("rice", "cake");

        HttpCookies httpCookies = new HttpCookies(params);
        HttpCookies otherHttpCookies = new HttpCookies(params);

        // when, then
        assertThat(httpCookies).isEqualTo(otherHttpCookies)
                .hasSameHashCodeAs(otherHttpCookies);
    }
}
