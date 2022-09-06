package org.apache.coyote.common.request.parser.bodyparser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.common.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("BodyParserMapper 는 ")
class BodyParserMapperTest {

    @DisplayName("MIME Type이 주어졌을 때 ")
    @Nested
    class BodyParserTest {

        @DisplayName("MIME Type이 유효하면 해당 타입 바디를 파싱한다.")
        @Test
        void parseValidMIMETypeBody() {
            final String body = "name=value";
            final Map<String, String> actual = BodyParserMapper.of(MediaType.MULTIPART_FORM_DATA)
                    .apply(body);

            assertThat(actual).isEqualTo(Map.of("name", "value"));
        }

        @DisplayName("MIME Type이 유효하지 않으면 바디를 있는 그대로 반환한다.")
        @Test
        void parseInvalidMIMETypeBody() {
            final String body = "a";
            final Map<String, String> actual = BodyParserMapper.of(MediaType.TEXT_HTML)
                    .apply(body);

            assertThat(actual).isEqualTo(Map.of("a", "a"));
        }
    }
}
