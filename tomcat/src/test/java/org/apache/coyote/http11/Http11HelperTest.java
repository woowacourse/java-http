package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11HelperTest {
    @DisplayName("확장자를 지정하지 않으면 default로 html 확장자로 지정한다.")
    @Test
    void getFileName_defaultExtension() {
        // when
        String fileName = Http11Helper.getInstance().getFileName("/index");

        // then
        assertThat(fileName).isEqualTo("index.html");
    }

    @DisplayName("파일 이름이 없는 경우 hello.html을 반환한다.")
    @Test
    void getFileName_defaultFileName() {
        // when
        String fileName = Http11Helper.getInstance().getFileName("/");

        // then
        assertThat(fileName).isEqualTo("hello.html");
    }

    @DisplayName("확장자가 있는 경우 해당 파일명을 그대로 반환한다.")
    @Test
    void getFileName_withExtension() {
        // when
        String fileName = Http11Helper.getInstance().getFileName("/style.css");

        // then
        assertThat(fileName).isEqualTo("style.css");
    }

    @DisplayName("쿼리스트링이 있을 때는 파일명에서 제외한다.")
    @Test
    void getFileName_withQueryString() {
        // when
        String fileName = Http11Helper.getInstance().getFileName("/index?user=1");

        // then
        assertThat(fileName).isEqualTo("index.html");
    }
}
