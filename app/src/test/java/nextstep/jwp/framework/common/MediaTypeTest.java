package nextstep.jwp.framework.common;

import nextstep.jwp.framework.file.FileExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MediaTypeTest {

    @DisplayName("값으로 MediaType 을 생성한다.")
    @Test
    void createWithValue() {
        MediaType mediaType = MediaType.from("text/html;charset=utf-8");
        assertThat(mediaType).isSameAs(MediaType.TEXT_HTML_CHARSET_UTF8);
    }

    @DisplayName("확장자로 MediaType 을 생성한다.")
    @Test
    void createWithExtension() {
        MediaType mediaType = MediaType.from(FileExtension.HTML);
        assertThat(mediaType).isSameAs(MediaType.TEXT_HTML_CHARSET_UTF8);
    }

    @DisplayName("존재하지 않는 값으로 MediaType 을 생성한다.")
    @Test
    void createWithInvalidValue() {
        assertThatThrownBy(() -> MediaType.from("ggyool/html"))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("존재하지 않는 값의 미디어 타입입니다");
    }
}
