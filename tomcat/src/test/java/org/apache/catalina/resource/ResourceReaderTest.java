package org.apache.catalina.resource;

import org.apache.coyote.http11.response.ContentType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ResourceReaderTest {

    @Test
    void 파일이_있으면_내용과_형식을_함께_읽는다() throws IOException {
        final Optional<Resource> resource = ResourceReader.read("/css/styles.css");

        assertThat(resource).isPresent();
        assertThat(resource.get().content()).isNotEmpty();
        assertThat(resource.get().contentType()).isEqualTo(ContentType.CSS);
    }

    @Test
    void 파일이_없으면_빈_값을_반환한다() throws IOException {
        final Optional<Resource> resource = ResourceReader.read("/nothing.html");

        assertThat(resource).isEmpty();
    }
}
