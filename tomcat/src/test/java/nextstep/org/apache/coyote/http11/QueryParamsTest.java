package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.exception.InvalidQueryParamKeyException;
import org.apache.coyote.http11.request.QueryParams;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    void of() {
        // given
        final String urlQueryParams = "id=abc&password=1234";

        // when
        final QueryParams queryParams = QueryParams.of(urlQueryParams);

        // then
        assertAll(
                () -> assertThat(queryParams.get("id")).isEqualTo("abc"),
                () -> assertThat(queryParams.get("password")).isEqualTo("1234")
        );
    }

    @Test
    void findNotContainingKey() {
        // given
        final String urlQueryParams = "id=abc&password=1234";

        // when
        final QueryParams queryParams = QueryParams.of(urlQueryParams);

        // then
        assertThatThrownBy(() -> queryParams.get("notExistKey"))
                .isExactlyInstanceOf(InvalidQueryParamKeyException.class);
    }
}
