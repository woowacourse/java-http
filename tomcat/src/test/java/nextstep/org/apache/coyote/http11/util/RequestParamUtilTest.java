package nextstep.org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.util.RequestParamUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("RequestParamUtil 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class RequestParamUtilTest {

    @Test
    void 리퀘스트_파라미터를_파싱한다() {
        // given
        String params = "mallang=123&name=1234";

        // when
        Map<String, String> parse = RequestParamUtil.parse(params);

        // then
        assertThat(parse.get("mallang")).isEqualTo("123");
        assertThat(parse.get("name")).isEqualTo("1234");
    }
}
