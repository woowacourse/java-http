package org.apache.coyote.http11.httpmessage.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class Http11VersionTest {

    @Test
    void Http11_버전을_반환한다() {
        // given
        String version = "HTTP/1.1";

        // when
        Http11Version http11Version = Http11Version.of(version);

        // then
        assertThat(http11Version.getVersion()).isEqualTo(version);
    }

    @Test
    void Http11_버전이_아닌경우_처리할_수_없다() {
        // given
        String version = "HTTP/3";

        // when & then
        assertThatThrownBy(() -> Http11Version.of(version))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
