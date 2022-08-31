package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NotFoundContentTypeException;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 없는_extension을_입력하는_경우_예외가_발생한다() {
        assertThatThrownBy(() -> ContentType.fromExtension("exe"))
                .isInstanceOf(NotFoundContentTypeException.class);
    }

    @Test
    void extension에_일치하는_contenttype으로_변환한다() {
        ContentType expected = ContentType.TEXT_HTML;
        ContentType actual = ContentType.fromExtension("html");

        assertThat(actual).isEqualTo(expected);
    }
}
