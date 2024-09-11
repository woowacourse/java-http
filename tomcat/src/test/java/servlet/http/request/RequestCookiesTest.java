package servlet.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class RequestCookiesTest {

    @Test
    void RequestCookies_객체를_생성한다() {
        // given
        String cookies = "tasty_cookie=choco; JSESSIONID=1234";

        // when
        RequestCookies actual = RequestCookies.from(cookies);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.get("tasty_cookie")).isEqualTo("choco");
            softly.assertThat(actual.get("JSESSIONID")).isEqualTo("1234");
        });
    }

    @Test
    void key_value_구분자가_없는_경우_예외가_발생한다() {
        // given
        String cookies = "tasty_cookie=choco; JSESSIONID";

        // when & then
        assertThatThrownBy(() -> RequestCookies.from(cookies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 Cookie입니다. request cookie: 'JSESSIONID'");
    }

    @Test
    void key_value_구분자가_두_개_이상일_경우_예외가_발생한다() {
        // given
        String cookies = "tasty_cookie=choco; JSESSIONID=1234=5678";

        // when & then
        assertThatThrownBy(() -> RequestCookies.from(cookies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 Cookie입니다. request cookie: 'JSESSIONID=1234=5678'");
    }

    @Test
    void cookie가_비어있을_경우_예외가_발생한다() {
        // given
        String cookies = "tasty_cookie=choco; ; JSESSIONID=1234";

        // when & then
        assertThatThrownBy(() -> RequestCookies.from(cookies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 Cookie입니다. request cookie: ''");
    }

    @Test
    void key가_비어있을_경우_예외가_발생한다() {
        // given
        String cookies = "tasty_cookie=choco; =1234";

        // when & then
        assertThatThrownBy(() -> RequestCookies.from(cookies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key 또는 value가 비어있습니다. key: '', value: '1234'");
    }

    @Test
    void value가_비어있을_경우_예외가_발생한다() {
        // given
        String cookies = "tasty_cookie=choco; JSESSIONID=";

        // when & then
        assertThatThrownBy(() -> RequestCookies.from(cookies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key 또는 value가 비어있습니다. key: 'JSESSIONID', value: ''");
    }

    @Test
    void key_value가_비어있을_경우_예외가_발생한다() {
        // given
        String cookies = "tasty_cookie=choco; =";

        // when & then
        assertThatThrownBy(() -> RequestCookies.from(cookies))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("key 또는 value가 비어있습니다. key: '', value: ''");
    }
}
