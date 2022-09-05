package nextstep.jwp.support;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceSuffixTest {

    @ParameterizedTest
    @CsvSource(value = {"/index.html,true", "/css/styles.css,true", "/js/scripts.js,true", "/login,false"})
    void 리소스_파일인지_확인한다(final String path, final boolean expected) {
        // given, when
        final boolean actual = ResourceSuffix.isEndWith(path);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
