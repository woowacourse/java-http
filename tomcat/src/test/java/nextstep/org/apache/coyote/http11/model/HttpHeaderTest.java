package nextstep.org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.apache.coyote.http11.model.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpHeaderTest {

    @Test
    @DisplayName("헤더에 key값이 존재하면 value를 반환한다.")
    void getAttribute() {
        // given
        HttpHeader header = HttpHeader.from(Map.of("account", "gugu"));

        // when, then
        assertThat(header.getAttributeOrDefault("account", "default")).isEqualTo("gugu");
    }

    @Test
    @DisplayName("헤더에 key값이 없으면 기본값을 반환한다.")
    void getDefaultValue() {
        // given
        HttpHeader header = HttpHeader.from(Map.of());

        // when, then
        assertThat(header.getAttributeOrDefault("account", "default")).isEqualTo("default");
    }
}
