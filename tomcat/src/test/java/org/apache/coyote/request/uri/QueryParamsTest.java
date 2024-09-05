package org.apache.coyote.request.uri;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    void QueryParams_객체를_생성한다() {
        // given
        String queryParams = "account=prin&password=1q2w3e4r!";

        // when
        QueryParams actual = QueryParams.from(queryParams);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getValue("account")).isEqualTo("prin");
            softly.assertThat(actual.getValue("password")).isEqualTo("1q2w3e4r!");
        });
    }

    @Test
    void QueryParam의_key가_존재하지_않을_경우_예외가_발생한댜() {
        // given
        String queryParams = "account=prin&password=1q2w3e4r!";

        // when
        QueryParams actual = QueryParams.from(queryParams);

        // then
        assertThatThrownBy(() -> actual.getValue("name"))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Query parameter가 존재하지 않습니다.");
    }
}
