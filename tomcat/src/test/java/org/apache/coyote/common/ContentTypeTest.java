package org.apache.coyote.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.request.Accept;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeTest {

    @Nested
    class Accept_헤더로_Content_Type_추출 {

        @Test
        void accept에_유효한_type이_존재하면_해당_타입을_반환한다() {
            final List<Accept> accepts = new ArrayList<>(
                    List.of(
                            new Accept("application/json", 1),
                            new Accept("text/html", 0.8)
                    )
            );

            final String result = ContentType.getTypeFrom(accepts);

            assertThat(result).isEqualTo("application/json");
        }

        @Test
        void accept에_유효한_type이_존재하지_않으면_null을_반환한다() {
            final List<Accept> accepts = new ArrayList<>(
                    List.of(
                            new Accept("application/text", 1),
                            new Accept("text/text", 0.8)
                    )
            );

            final String result = ContentType.getTypeFrom(accepts);

            assertThat(result).isNull();
        }
    }

    @Nested
    class 확장자로_Content_Type_추출 {

        @Test
        void 존재하는_확장자라면_해당_타입을_반환한다() {
            final String extension = "js";

            final String result = ContentType.getTypeFrom(extension);

            assertThat(result).isEqualTo("application/javascript");
        }

        @Test
        void 존재하지_않는_확장자라면_null을_반환한다() {
            final String extension = "abc";

            final String result = ContentType.getTypeFrom(extension);

            assertThat(result).isNull();
        }
    }
}
