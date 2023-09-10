package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseBodyTest {

    @Nested
    class 객체_생성_테스트 {

        @Test
        void URI로_ResponseBody_객체를_생성한다() throws IOException {
            // given
            final var uri = "/index.html";

            final var fileUrl = ResponseBody.class.getClassLoader().getResource("./static" + uri);
            final var filePath = Objects.requireNonNull(fileUrl).getPath();
            final var expected = Files.readString(new File(filePath).toPath());

            // when
            final var actual = ResponseBody.fromUri(uri);

            // then
            assertThat(actual.getBody()).isEqualTo(expected);
        }

        @Test
        void text로_ResponseBody_객체를_생성한다() {
            // given
            final var text = "Hello World!";

            // when
            final var actual = ResponseBody.fromText(text);

            // then
            assertThat(actual.getBody()).isEqualTo(text);
        }
    }
}
