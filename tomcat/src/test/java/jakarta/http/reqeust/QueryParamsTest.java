package jakarta.http.reqeust;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.QueryStringFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @DisplayName("QueryString이 공백이면 예외를 발생한다")
    @Test
    void query_blank_exception() {
        // given
        String query = " ";

        // when & then
        assertThatThrownBy(() -> new QueryParams(query))
                .isInstanceOf(QueryStringFormatException.class);
    }

    @DisplayName("QueryString의 사이즈가 2가 아니면 예외를 발생한다")
    @Test
    void query_size_exception() {
        // given
        String query = "account=";

        // when & then
        assertThatThrownBy(() -> new QueryParams(query))
                .isInstanceOf(QueryStringFormatException.class);
    }

    @DisplayName("QueryParams 객체를 생성한다.")
    @Test
    void construct_query() {
        // given
        String query = "account=33";

        // when
        QueryParams queryParams = new QueryParams(query);

        // then
        assertThat(queryParams).isNotNull();
    }
}
