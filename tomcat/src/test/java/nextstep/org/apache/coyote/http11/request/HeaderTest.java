package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.request.Header;
import org.apache.coyote.http11.request.InvalidHeaderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("Header 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HeaderTest {

    @Test
    void Header가_null_이면_예외() {
        // when & then
        assertThatThrownBy(() ->
                Header.from(null)
        ).isInstanceOf(InvalidHeaderException.class);
    }

    @Test
    void Header가_올바르지_않은_형식이면_예외() {
        // given
        String invalidHeader = "Host:";

        // when & then
        assertThatThrownBy(() ->
                Header.from(invalidHeader)
        ).isInstanceOf(InvalidHeaderException.class);
    }

    @Test
    void 올바르다면_잘_생성된다() {
        // given
        String valid = "Host: localhost:8080";

        // when
        Header header = Header.from(valid);

        // then
        assertThat(header.name()).isEqualTo("Host");
        assertThat(header.value()).isEqualTo("localhost:8080");
    }
}
