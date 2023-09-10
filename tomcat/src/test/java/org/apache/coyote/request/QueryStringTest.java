package org.apache.coyote.request;

import org.apache.exception.QueryParamsNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class QueryStringTest {

    @Test
    void 쿼리가_들어있지_않아도_생성된다() {
        final QueryString queryString = QueryString.from("");
        assertThat(queryString).hasToString("");
    }

    @Test
    void 쿼리가_들어있지_않았을때_조회하면_예외가_발생한다() {
        final QueryString queryString = QueryString.from("");

        assertThatThrownBy(() -> queryString.getQueryValueBy("name"))
                .isExactlyInstanceOf(QueryParamsNotFoundException.class);
    }

    @Test
    void 쿼리가_담겨_있으면_조회할_수_있다() {
        final QueryString queryString = QueryString.from("?name=kero");
        final String name = queryString.getQueryValueBy("name");
        assertThat(name).isEqualTo("kero");
    }
}
