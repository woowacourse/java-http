package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

class ResourcesUtilTest {

    @Test
    void 확장자_가져오기_테스트() {
        // given
        String fileName = "nextstep.txt";

        // when
        String actual = ResourcesUtil.parseExtension(fileName);

        // then
        assertThat(actual).isEqualTo("txt");
    }

    @Test
    void resource_읽기_테스트() throws IOException {
        // given
        String fileName = "nextstep.txt";

        // when
        String actual = ResourcesUtil.readResource(fileName);

        // then
        URL resource = getClass().getClassLoader().getResource(fileName);
        assert resource != null;
        String expected = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 없는_resource를_읽으려_하면_예외를_반환한다() throws IOException {
        // given
        String fileName = "invalid resource";

        // when, then
        assertThatThrownBy(() -> ResourcesUtil.readResource(fileName))
                .isExactlyInstanceOf(ResourceNotFoundException.class);
    }
}
