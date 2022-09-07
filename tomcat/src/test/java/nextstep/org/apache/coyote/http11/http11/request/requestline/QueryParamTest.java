package nextstep.org.apache.coyote.http11.http11.request.requestline;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.domain.request.requestline.QueryParam;
import org.junit.jupiter.api.Test;

public class QueryParamTest {

    @Test
    void createQueryParam() {
        // given
        String queryString = "account=gugu&password=password";
        // when
        QueryParam queryParam = QueryParam.from(queryString);
        // then
        assertAll(
                () -> assertThat(queryParam.getQueryValue("account")).isEqualTo("gugu"),
                () -> assertThat(queryParam.getQueryValue("password")).isEqualTo("password")
        );
    }
}
