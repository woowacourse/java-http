package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookiesTest {
    @Test
    @DisplayName("Cookie에 해당하는 String을 바탕으로 Cookies 객체를 만들었을 때, 정상적으로 Map의 형태로 Cookie를 반환한다.")
    void getCookies() {
        Cookies cookies = new Cookies("key1=value1; key2=value2");
        Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("key2", "value2");

        expected.forEach((key, value) -> {
            assertThat(cookies.getCookies()).containsEntry(key, value);
        });
    }
}