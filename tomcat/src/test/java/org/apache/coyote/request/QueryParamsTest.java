package org.apache.coyote.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QueryParamsTest {

    @Test
    void URI를_이용해서_생성에_성공한다() {
        // given
        final String uri = "/index.html?name=헤나&gender=male";

        // when
        final QueryParams queryParams = QueryParams.from(uri);

        // then
        assertAll(
                () -> assertThat(queryParams.paramNames()).contains("name", "gender"),
                () -> assertThat(queryParams.getParamValue("name")).contains("헤나"),
                () -> assertThat(queryParams.getParamValue("gender")).contains("male")
        );
    }
}
