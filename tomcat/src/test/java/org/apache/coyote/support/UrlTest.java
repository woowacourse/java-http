package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import org.apache.coyote.exception.InvalidUrlException;
import org.junit.jupiter.api.Test;

class UrlTest {

    @Test
    void createUrlInvalid() {
        assertThatThrownBy(
                () -> Url.createUrl("aaaa")
        ).isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void isDefaultPath() {
        Url url = Url.createUrl("/");
        assertThat(url.isDefaultPath()).isTrue();
    }

    @Test
    void extractFileExtension() {
        Url url = Url.createUrl("/index.html");
        assertThat(url.extractFileExtension()).isEqualTo("html");
    }

    @Test
    void extractFileLines() throws IOException {
        Url url = Url.createUrl("/index.html");
        assertThat(url.extractFileLines()).contains("대시보드");
    }
}
