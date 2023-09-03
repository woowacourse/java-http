package org.apache.coyote.request;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AcceptTest {

    @Nested
    class ACCEPT_추출 {

        @Test
        void 품질_요소가_존재하면_품질값으로_설정된다() {
            final String[] accepts = new String[]{"text/html", "q=0.5"};

            final Accept accept = Accept.from(accepts);

            assertSoftly(softly -> {
                softly.assertThat(accept.getAcceptType()).isEqualTo("text/html");
                softly.assertThat(accept.getQuality()).isEqualTo(0.5);
            });
        }

        @Test
        void 품질_요소가_존재하지_않으면_0으로_설정된다() {
            final String[] accepts = new String[]{"text/html"};

            final Accept accept = Accept.from(accepts);

            assertSoftly(softly -> {
                softly.assertThat(accept.getAcceptType()).isEqualTo("text/html");
                softly.assertThat(accept.getQuality()).isEqualTo(0);
            });
        }
    }
}
