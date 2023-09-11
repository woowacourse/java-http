package common.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpMethodTest {
    @Test
    void 메서드_이름으로_Http_Method를_가져온다() {
        // given
        String get = "GET";

        // expect
        assertThat(HttpMethod.parseHttpMethod(get)).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 유효하지_않은_메서드_이름이면_예외를_반환한다() {
        // given
        String 가져온나 = "가져온나";

        // expect
        Assertions.assertThatThrownBy(() -> HttpMethod.parseHttpMethod(가져온나))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 HTTP 메서드입니다.");
    }
}
