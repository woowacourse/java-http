package nextstep.jwp.protocol.request_line.vo;

import org.apache.coyote.http11.request.line.vo.QueryString;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryStringTest {

    @Nested
    class 쿼리스트링_생성을_검증한다 {

        @Test
        void 쿼리스트링이_유효하다면_생성한다() {
            // given
            final String request = "name=베베";
            QueryString queryString = QueryString.from(request);

            // when, then
            assertThat(queryString.value().get("name"))
                    .isEqualTo("베베");
        }

        @Test
        void 쿼리스트링_여러개를_생성한다() {
            // given
            final String request = "name=베베&age=19";
            QueryString queryString = QueryString.from(request);

            // when, then
            assertAll(
                    () -> assertThat(queryString.value().get("name"))
                            .isEqualTo("베베"),
                    () -> assertThat(queryString.value().get("age"))
                            .isEqualTo("19")
            );
        }

        @Test
        void 쿼리스트링이_유효하지_않다면_예외가_발생한다() {
            // given
            final String queryString = "name=베베??";

            // when, then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> QueryString.from(queryString)
            );
        }

        @Test
        void 쿼리스트링이_Null_이라면_value는_null이다() {
            // given
            final String request = null;
            QueryString queryString = QueryString.from(request);

            // when, then
            assertThat(queryString.value()).isNull();
        }

    }
}
