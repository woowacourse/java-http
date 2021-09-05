package nextstep.jwp.framework.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import nextstep.jwp.framework.http.common.QueryParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParamsTest {

    @DisplayName("쿼리 파라미터를 생성한다.")
    @Test
    void create() {
        //given
        QueryParams queryParams = new QueryParams("account=wilder&password=123");

        //when
        Map<String, String> actual = queryParams.getValue();

        //then
        assertThat(queryParams.count()).isEqualTo(2);
        assertThat(actual.get("account")).isEqualTo("wilder");
        assertThat(actual.get("password")).isEqualTo("123");
    }

    @DisplayName("쿼리 파라미터를 출력한다.")
    @Test
    void print() {
        //given
        QueryParams queryParams = new QueryParams("account=wilder&password=123");

        //when
        String actual = queryParams.getValue().toString();

        //then
        assertThat(actual).isEqualTo("{password=123, account=wilder}");
    }
}
