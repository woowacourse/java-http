package org.apache.coyote.utils;

import org.apache.coyote.exception.NotFoundFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestUtilTest {

    @Test
    @DisplayName("파일이름으로 파일내용을 읽는다.")
    void checkFileByFilename() {
        // given
        String filename = "/index.html";

        // when
        String actual = RequestUtil.getResponseBody(filename, this.getClass());

        // then
        assertThat(actual).isNotBlank();
    }

    @Test
    @DisplayName("파일이름이 없는 경우 예외를 발생한다.")
    void checkNonFile() {
        // given
        String filename = "/nonFile";

        // when & then
        assertThatThrownBy(() -> RequestUtil.getResponseBody(filename, this.getClass()))
                .isInstanceOf(NotFoundFileException.class);
    }

    @Test
    @DisplayName("uri를 통해 확장자를 추출한다.")
    void checkGetExtension() {
        // given
        String filename = "/index.html";

        // when
        String actual = RequestUtil.getExtension(filename);

        // then
        assertThat(actual).isEqualTo("html");
    }

    @Test
    @DisplayName("uri를 통해 path를 추출한다.")
    void checkGetPath() {
        // given
        String filename = "/login?account=gugu&password=password";

        // when
        String actual = RequestUtil.calculatePath(filename);

        // then
        assertThat(actual).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("uri를 통해 param을 추출한다.")
    void checkGetParam() {
        // given
        String filename = "/login?account=gugu&password=password";
        Map<String, String> expected = new HashMap<>();
        expected.put("account", "gugu");
        expected.put("password", "password");

        // when
        Map<String, String> actual = RequestUtil.getParam(filename);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
