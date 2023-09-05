package org.apache.coyote.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QueryParamsTest {

    @Test
    void URI에_쿼리_파라미터를_이용해서_생성에_성공한다() {
        // given
        final String uri = "/index.html?name=헤나&gender=male";

        // expect
        assertThatCode(() -> QueryParams.from(uri))
                .doesNotThrowAnyException();
    }

    @Test
    void 쿼리_파라미터를_이용해서_생성에_성공한다() {
        // given
        final String queryParamsValue = "name=헤나&gender=male";

        // expect
        assertThatCode(() -> QueryParams.from(queryParamsValue))
                .doesNotThrowAnyException();
    }

    @Test
    void URI를_이용한_생성과_쿼리_파라미터를_이용한_생성은_동일하다() {
        // given
        final String uri = "/index.html?name=헤나&gender=male";
        final String queryParamsValue = "name=헤나&gender=male";

        // when
        final QueryParams queryParamsByUri = QueryParams.from(uri);
        final QueryParams queryParamsByValue = QueryParams.from(queryParamsValue);

        // expect
        assertThat(queryParamsByUri).isEqualTo(queryParamsByValue);
    }

    @Test
    void 쿼리_파라미터의_키값_목록을_가져온다() {
        // given
        final String uri = "/index.html?name=헤나&gender=male";

        // when
        final QueryParams actual = QueryParams.from(uri);

        // then
        assertThat(actual.paramNames()).contains("name", "gender");
    }

    @Test
    void 쿼리_파라미터의_키값에_매칭되는_값을_가져온다() {
        // given
        final String uri = "/index.html?name=헤나&gender=male";

        // when
        final QueryParams actual = QueryParams.from(uri);

        // then
        assertAll(
                () -> assertThat(actual.getParamValue("name")).contains("헤나"),
                () -> assertThat(actual.getParamValue("gender")).contains("male")
        );
    }
}
