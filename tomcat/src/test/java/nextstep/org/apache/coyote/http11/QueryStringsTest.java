package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.QueryStrings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringsTest {

    @Test
    @DisplayName("문자열 형태의 쿼리스트링을 파싱하여 생성한다.")
    void parseQueryString() {
        // given
        final String queryStrings = "account=gugu&password=password";

        // when
        final QueryStrings httpQueryStrings = new QueryStrings(queryStrings);

        // then
        assertAll(
                () -> assertThat(httpQueryStrings.getValue("account")).isEqualTo("gugu"),
                () -> assertThat(httpQueryStrings.getValue("password")).isEqualTo("password"),
                () -> assertThat(httpQueryStrings.getValue("invalid")).isEqualTo("")
        );
    }
}
