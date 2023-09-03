package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ContentTypeTest {

    @Test
    @DisplayName("파일의 확장자로 ContentType을 생성할 수 있다")
    void construct() {
        //given
        final String file = "index.html";

        //when, then
        assertDoesNotThrow(() -> ContentType.from(file));
    }

    @Test
    @DisplayName("존재하지 않는 타입으로 생성하면 예외가 발생한다")
    void construct_fail() {
        assertThatThrownBy(() -> ContentType.from("nothing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("허용되지 않는 타입입니다.");
    }

    @Test
    @DisplayName("ContentType 헤더를 생성할 수 있다")
    void toHeader() {
        //given
        final String file = "index.html";
        final ContentType contentType = ContentType.from(file);

        //when
        final String header = contentType.toHeader();

        //then
        final String expected = "Content-Type: text/html;charset=utf-8 ";
        assertThat(header).isEqualTo(expected);
    }
}
