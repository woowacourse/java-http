package nextstep.jwp.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class QueryStringUtilTest {

    @Test
    void 문자열을_파싱해서_키_값_형태로_맵에_저장해_반환한다() {
        final String queryString = "account=irene&password=1234";
        final Map<String, String> parse = QueryStringUtil.parse(queryString);

        assertThat(parse).containsEntry("account", "irene")
                .containsEntry("password", "1234");
    }

    @Test
    void 문자열이_null이면_빈_맵을_반환한다() {
        final String queryString = null;
        final Map<String, String> parse = QueryStringUtil.parse(queryString);

        assertThat(parse).isEmpty();
    }
}
