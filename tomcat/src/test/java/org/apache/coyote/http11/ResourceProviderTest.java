package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ResourceProviderTest {

    private static final String INDEX_FILE_URI = "/index.html";
    private static final String NOT_EXIST_FILE_URI = "/notExist.html";
    ResourceProvider resourceProvider = new ResourceProvider();

    @Test
    void Resource_privier_는_존재하는_파일이면_true_반환() {
        // giveh & when
        boolean exist = resourceProvider.haveResource(INDEX_FILE_URI);

        // then
        assertThat(exist).isTrue();
    }

    @Test
    void Resource_privier_는_존재하는_파일이면_false_반환() {
        // giveh & when
        boolean exist = resourceProvider.haveResource(NOT_EXIST_FILE_URI);

        // then
        assertThat(exist).isFalse();
    }

    @Test
    void 존재하는_파일이면_파일_내용을_반환() {
        // given & when
        String fileBody = resourceProvider.resourceBodyOf(INDEX_FILE_URI);

        // then
        assertThat(fileBody).isNotNull();
    }

    @Test
    void 존재하지_않는_파일이면_예외() {
        // given & when & then
        assertThatThrownBy(() -> resourceProvider.resourceBodyOf(NOT_EXIST_FILE_URI))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일이 존재하지 않습니다.");
    }

    @Test
    void 해당_리소스의_타입_반환() {
        // given & when
        String contentTypeOf = resourceProvider.contentTypeOf(INDEX_FILE_URI);

        // then
        assertThat(contentTypeOf).isEqualTo("Content-Type: text/html;charset=utf-8 ");
    }

    @Test
    void 파일이_없으면_타입_반환은_에러가_빨생한다() {
        // given & when & then
        assertThatThrownBy(() -> resourceProvider.contentTypeOf(NOT_EXIST_FILE_URI))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("파일이 존재하지 않습니다.");
    }
}
