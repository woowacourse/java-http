package org.apache.coyote.http11.response.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("extension을 통해 정적 리소스인지 판단한다.")
    @Test
    void isStaticResourceTest() {
        assertAll(
                () -> assertThat(ContentType.isStaticResource("/index.html")).isTrue(),
                () -> assertThat(ContentType.isStaticResource("/style.css")).isTrue(),
                () -> assertThat(ContentType.isStaticResource("/login")).isFalse()
        );
    }

    @DisplayName("extension과 일치하는 ContentType을 반환한다.")
    @Test
    void successFindTest() {
        assertAll(
                () -> assertThat(ContentType.findByExtension("/index.html")).isEqualTo(ContentType.HTML),
                () -> assertThat(ContentType.findByExtension("/index.css")).isEqualTo(ContentType.CSS)
        );
    }

    @DisplayName("extension과 일치하는 ContentType이 없으면 text/html을 반환한다.")
    @Test
    void failureFindTest() {
        assertThat(ContentType.findByExtension("/index.html")).isEqualTo(ContentType.HTML);
    }

    @DisplayName("mediaType과 일치하는 ContentType을 반환한다.")
    @Test
    void successFindByMediaTypeTest() {
        assertAll(
                () -> assertThat(ContentType.findByMediaType("text/html")).isEqualTo(ContentType.HTML),
                () -> assertThat(ContentType.findByMediaType("text/css")).isEqualTo(ContentType.CSS),
                () -> assertThat(ContentType.findByMediaType("text/javascript")).isEqualTo(ContentType.JS),
                () -> assertThat(ContentType.findByMediaType("image/x-icon")).isEqualTo(ContentType.ICO),
                () -> assertThat(ContentType.findByMediaType("application/x-www-form-urlencoded")).isEqualTo(
                        ContentType.APPLICATION_X_WWW_FORM_URLENCODED)
        );
    }

    @DisplayName("mediaType과 일치하는 ContentType이 없으면 예외를 반환한다.")
    @Test
    void failureFindByMediaTypeTest() {
        assertThatThrownBy(() -> ContentType.findByMediaType("application/unknown"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
