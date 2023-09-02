package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.HttpExtensionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpExtensionTypeTest {

    @DisplayName("contentType을 올바르게 가져올 수 있다.")
    @Test
    void from() {
        // given
        final String extension = "test.css";
        final String expected = "text/css";

        // when
        final HttpExtensionType actual = HttpExtensionType.from(extension);

        // then
        assertThat(actual.getContentType()).isEqualTo(expected);
    }

    @DisplayName("ExtensionType에 해당하는 확장자가 없으면 HTML 타입을 반환한다.")
    @Test
    void from_emptyExtension() {
        // given
        final String extension = "EmptyExtension";
        final String expected = "text/html";

        // when
        final HttpExtensionType actual = HttpExtensionType.from(extension);

        // then
        assertThat(actual.getContentType()).isEqualTo(expected);
    }
}
