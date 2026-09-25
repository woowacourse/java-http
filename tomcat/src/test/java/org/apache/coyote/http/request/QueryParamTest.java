package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class QueryParamTest {

    @Test
    void 쿼리_문자열을_파싱한다() {
        final QueryParam queryParam = QueryParam.from("account=gugu&password=password");

        assertThat(queryParam.get("account")).contains("gugu");
        assertThat(queryParam.get("password")).contains("password");
    }

    @Test
    void 중복된_키는_첫_번째_값을_사용한다() {
        final QueryParam queryParam = QueryParam.from("id=1&id=2");

        assertThat(queryParam.get("id")).contains("1");
    }

    @Test
    void 존재하지_않는_키는_빈_값을_반환한다() {
        assertThat(QueryParam.from("id=1").get("account")).isEmpty();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 쿼리가_없으면_빈_값을_반환한다(final String rawQuery) {
        assertThat(QueryParam.from(rawQuery).get("id")).isEmpty();
    }

    @Test
    void 값이_없는_파라미터는_빈_값으로_처리한다() {
        final QueryParam queryParam = QueryParam.from("id=1&flag");

        assertThat(queryParam.get("id")).contains("1");
        assertThat(queryParam.get("flag")).contains("");
    }
}
