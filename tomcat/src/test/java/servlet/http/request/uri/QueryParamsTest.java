package servlet.http.request.uri;

import static org.assertj.core.api.Assertions.assertThat;
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
            softly.assertThat(actual.get("account")).isEqualTo("prin");
            softly.assertThat(actual.get("password")).isEqualTo("1q2w3e4r!");
        });
    }

    @Test
    void QueryParams가_비어있을_경우_key를_조회할_때_예외가_발생한다() {
        // given
        QueryParams queryParams = QueryParams.from(null);

        // when & then
        assertThatThrownBy(() -> queryParams.get("any"))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Query parameter가 비어있습니다.");
    }

    @Test
    void QueryParams에_해당_key가_존재하지_않을_경우_예외가_발생한댜() {
        // given
        String params = "account=prin&password=1q2w3e4r!";
        QueryParams queryParams = QueryParams.from(params);

        // when & then
        assertThatThrownBy(() -> queryParams.get("name"))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Query parameter에 해당 key가 존재하지 않습니다. key: name");
    }

    @Test
    void QueryParams가_존재하면_true를_반환한다() {
        // given
        String params = "account=prin&password=1q2w3e4r!";
        QueryParams queryParams = QueryParams.from(params);

        // when
        boolean actual = queryParams.existQueryParams();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void QueryParams가_존재하지_않으면_false를_반환한다() {
        // given
        QueryParams queryParams = QueryParams.from(null);

        // when
        boolean actual = queryParams.existQueryParams();

        // then
        assertThat(actual).isFalse();
    }
}
