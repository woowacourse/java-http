package org.apache.coyote.utils;

import org.apache.coyote.exception.NotFoundFileException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.model.request.RequestLine.calculatePath;
import static org.apache.coyote.model.request.RequestLine.getExtension;
import static org.apache.coyote.model.request.RequestLine.getParam;
import static org.apache.coyote.model.response.HttpResponse.getResponseBody;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UtilTest {

    @Test
    @DisplayName("파일이름으로 파일내용을 읽는다.")
    void checkFileByFilename() {
        // given
        String filename = "/index.html";

        // when
        String actual = getResponseBody(filename, this.getClass());

        // then
        assertThat(actual).isNotBlank();
    }

    @Test
    @DisplayName("파일이름이 없는 경우 예외를 발생한다.")
    void checkNonFile() {
        // given
        String filename = "/nonFile";

        // when & then
        assertThatThrownBy(() -> getResponseBody(filename, this.getClass()))
                .isInstanceOf(NotFoundFileException.class);
    }

    @Test
    @DisplayName("uri를 통해 확장자를 추출한다.")
    void checkGetExtension() {
        // given
        String filename = "/index.html";

        // when
        String actual = getExtension(filename);

        // then
        assertThat(actual).isEqualTo("html");
    }

    @Test
    @DisplayName("uri를 통해 path를 추출한다.")
    void checkGetPath() {
        // given
        String filename = "/login?account=gugu&password=password";

        // when
        String actual = calculatePath(filename);

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
        Map<String, String> actual = getParam(filename);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
