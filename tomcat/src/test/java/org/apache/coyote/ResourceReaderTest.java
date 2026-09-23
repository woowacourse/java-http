package org.apache.coyote;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceReaderTest {

    private final ResourceReader resourceReader = new ResourceReader();

    @Test
    void classpath의_리소스를_읽는다() throws Exception {

        // when
        final Optional<byte[]> resource =
                resourceReader.read("static/login.html");

        // then
        assertThat(resource).isPresent();

        final String content = new String(
                resource.orElseThrow(),
                StandardCharsets.UTF_8
        );

        assertThat(content).contains("<title>로그인</title>");
    }

    @Test
    void 존재하지_않는_리소스면_empty를_반환한다() throws Exception {

        // when
        final Optional<byte[]> resource =
                resourceReader.read("static/not-found-resource.html");

        // then
        assertThat(resource).isEmpty();
    }
}