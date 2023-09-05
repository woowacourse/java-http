package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.exception.http.InvalidRequestPathException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestPathTest {

    @Nested
    class 요청_경로_추출 {

        @Test
        void 올바른_요청_경로라면_요청_경로_정보를_추출한다() {
            final String path = "/login?account=gugu&password=password";

            final RequestPath requestPath = RequestPath.from(path);

            assertSoftly(softly -> {
                softly.assertThat(requestPath.getUriPath()).isEqualTo("/login");
                softly.assertThat(requestPath.getParameter("account")).isEqualTo("gugu");
                softly.assertThat(requestPath.getParameter("password")).isEqualTo("password");
            });
        }

        @Test
        void 요청_경로가_null이라면_예외를_던진다() {
            assertThatThrownBy(() -> RequestPath.from(null))
                    .isInstanceOf(InvalidRequestPathException.class)
                    .hasMessage("Path is Null Or Empty");
        }

        @Test
        void 요청_경로가_비어있으면_예외를_던진다() {
            assertThatThrownBy(() -> RequestPath.from(""))
                    .isInstanceOf(InvalidRequestPathException.class)
                    .hasMessage("Path is Null Or Empty");
        }

        @Test
        void 요청_파라미터가_올바르지_않으면_예외를_던진다() {
            final String path = "/login?account=gugu&password=";

            assertThatThrownBy(() -> RequestPath.from(path))
                    .isInstanceOf(InvalidRequestPathException.class)
                    .hasMessage("Parameter Element Count Not Match");
        }
    }
}
