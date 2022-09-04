package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @Test
    @DisplayName("키를 전달하면 값을 가져올 수 있다.")
    void get_success() {
        QueryParams queryParams = QueryParams.from("account=leo&password=password");

        String actual = queryParams.get("account");

        String expected = "leo";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("올바르지 않은 키로 값을 가져오려고 하면 예외가 발생한다.")
    void get_fail() {
        QueryParams queryParams = QueryParams.from("password=password");

        assertThatThrownBy(() -> queryParams.get("account"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바르지 않은 키 입니다.");
    }

    @Test
    @DisplayName("키를 가지고 있으면 true를 반환한다.")
    void contains_success() {
        QueryParams queryParams = QueryParams.from("account=leo&password=password");

        boolean actual = queryParams.contains("account");

        assertThat(actual).isTrue();
    }
}
