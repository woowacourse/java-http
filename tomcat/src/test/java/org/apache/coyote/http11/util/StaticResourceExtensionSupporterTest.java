package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StaticResourceExtensionSupporterTest {

    @Test
    void URI가_지원하는_확장자인_경우_true를_반환한다() {
        // given
        final var uri = "/index.html";

        // when
        final var actual = StaticResourceExtensionSupporter.isStaticResource(uri);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void URI가_지원하는_확장자가_아닌경우_false를_반환한다() {
        // given
        final var uri = "/index.invalidExtension";

        // when
        final var actual = StaticResourceExtensionSupporter.isStaticResource(uri);

        // then
        assertThat(actual).isFalse();
    }
}