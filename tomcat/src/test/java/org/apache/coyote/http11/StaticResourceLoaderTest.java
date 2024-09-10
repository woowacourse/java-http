package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StaticResourceLoaderTest {

    @DisplayName("경로에 대한 정적 파일 내용을 불러온다.")
    @Test
    void loadStaticResource() {
        // given
        String path = "/someResource.txt";
        String expected = "hello test\n";
        StaticResourceLoader loader = new StaticResourceLoader();

        // when
        String actual = loader.load(path);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("확장자가 존재하지 않을 경우 .html 확장자인 파일을 읽는다.")
    @Test
    void loadStaticResourceWithoutExtension() {
        // given
        String path = "/register";
        String expected = "register content\n";
        StaticResourceLoader loader = new StaticResourceLoader();

        // when
        String actual = loader.load(path);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
