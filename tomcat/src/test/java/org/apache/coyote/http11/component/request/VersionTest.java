package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.apache.coyote.http11.component.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VersionTest {

    @Test
    @DisplayName("Http version을 관리하는 객체를 생성한다.")
    void generate_exist_http_version_control_class() {
        // given
        final var text = "HTTP/1.1";

        // when & then
        assertThatCode(() -> new Version(text))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("존재하지 않는 version이면 예외를 발생한다.")
    void throw_exception_when_generate_does_not_exist_http_version() {
        // given
        final var text = "HTTP/1.3";

        // when & then
        assertThatThrownBy(() -> new Version(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("버전 프로토콜 prefix 불일치시 예외를 발생시킨다.")
    void throw_exception_when_protocol_prefix_does_not_match() {
        // given
        final var text = "FTP/1.1";

        // when & then
        assertThatThrownBy(() -> new Version(text))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("응답을 위한 버전 문자 생성한다.")
    void generate_version_info_for_response() {
        // given
        final var version = new Version(1, 1);

        // when
        final var responseText = version.getResponseText();

        // then
        assertThat(responseText).isEqualTo("HTTP/1.1");
    }
}
