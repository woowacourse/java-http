package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {
    @DisplayName("ResponseBody의 바이트 단위 길이를 구한다.")
    @Test
    void parseRequestBody() throws IOException {
        // given
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        ResponseBody responseBody = new ResponseBody(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        // when
        int result = responseBody.getLength();

        // then
        assertThat(result).isEqualTo(5564);
    }
}
