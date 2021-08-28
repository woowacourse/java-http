package nextstep.jwp.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class QueryParamTest {

    @DisplayName("쿼리 파라미터로 저장된 값을 조회해올 수 있다.")
    @Test
    void searchValue() {
        final QueryParam queryParam = QueryParam.of("user=joel&password=pw");
        final String user = queryParam.searchValue("user");
        final String password = queryParam.searchValue("password");

        assertThat(user).isEqualTo("joel");
        assertThat(password).isEqualTo("pw");
    }

    @DisplayName("쿼리 파라미터에 해당 값이 저장되어 있지 않으면 빈 문자열을 반환한다.")
    @Test
    void SearchEmptyValue() {
        final QueryParam queryParam = QueryParam.of("user=joel&password=pw");
        final String nothing = queryParam.searchValue("nothing");

        assertThat(nothing).isEqualTo("");
    }
}