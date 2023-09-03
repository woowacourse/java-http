package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.InvalidUriPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisteredUriTest {

    @Test
    @DisplayName("등록된 uriPath가 있으면, path + .html을 반환한다.")
    void getPathWithExtension() {
        //given
        final String path = "/login";

        //when
        final String pathWithExtension = RegisteredUri.getPathWithExtension(path);

        //then
        assertThat(pathWithExtension).isEqualTo(path + ".html");
    }

    @Test
    @DisplayName("등록된 uriPath가 아닌 경우 예외가 발생한다.")
    void getPathWithExtension_fail() {
        //given
        final String path = "/invalid";

        //when & then
        assertThatThrownBy(() -> RegisteredUri.getPathWithExtension(path))
                .isInstanceOf(InvalidUriPath.class)
                .hasMessage("등록되지 않은 URI입니다.");
    }
}
