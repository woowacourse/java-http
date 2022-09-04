package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamTest {

    @Test
    @DisplayName("key=value 형식의 query parameter 를 구문 분석한다.")
    void from_success() {
        String queryParam = "key=value";

        QueryParam actual = QueryParam.from(queryParam);

        QueryParam expected = new QueryParam("key", "value");
        assertThat(actual).usingRecursiveComparison()
            .isEqualTo(expected);
    }
}
