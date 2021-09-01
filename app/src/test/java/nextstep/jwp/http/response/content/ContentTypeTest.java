package nextstep.jwp.http.response.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("findContentType Test")
    @Test
    void findContentType() {
        //given
        String extension = ".html";
        //when
        ContentType contentType = ContentType.findContentType(extension);
        //then
        assertThat(contentType).isEqualTo(ContentType.HTML);
    }

    @DisplayName("notExist ContentType Test")
    @Test
    void notExistContentType() {
        //given
        String extension = "notExistExtension";
        //then
        assertThatThrownBy(() -> ContentType.findContentType(extension))
                .isInstanceOf(RuntimeException.class);
    }
}