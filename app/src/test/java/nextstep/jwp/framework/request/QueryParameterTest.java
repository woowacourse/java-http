package nextstep.jwp.framework.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class QueryParameterTest {

    @DisplayName("쿼리 파라미터로 저장된 값을 조회해올 수 있다.")
    @Test
    void searchValue() {
        final QueryParameter queryParameter = QueryParameter.of("user=joel&password=pw");
        final String user = queryParameter.searchValue("user");
        final String password = queryParameter.searchValue("password");

        assertThat(user).isEqualTo("joel");
        assertThat(password).isEqualTo("pw");
    }

    @DisplayName("쿼리 파라미터에 해당 값이 저장되어 있지 않으면 빈 문자열을 반환한다.")
    @Test
    void SearchEmptyValue() {
        final QueryParameter queryParameter = QueryParameter.of("user=joel&password=pw");
        final String nothing = queryParameter.searchValue("nothing");

        assertThat(nothing).isEmpty();
    }
}