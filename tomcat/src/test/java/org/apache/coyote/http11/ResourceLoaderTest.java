package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.util.ResourceLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.io.IOException;

class ResourceLoaderTest {

    @DisplayName("URI에 해당하는 컨텐츠를 불러온다.")
    @ParameterizedTest
    @CsvSource(value = {
            "/,Hello", "/index.html,대시보드", "/login.html,로그인"})
    void getContent(final String uri, final String expectedWord) throws IOException {
        // given, when
        final String content = ResourceLoader.getContent(uri);

        // then
        assertThat(content).contains(expectedWord);
    }
}
