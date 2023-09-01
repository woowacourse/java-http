package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestUtilTest {

    @Test
    void queryString을_입력받아_Key_Value_형태로_변환한다() {
        // given
        final String queryString = "account=gugu&password=password";

        // when
        final Map<String, String> queryStrings = HttpRequestUtil.parseQueryString(queryString);

        // then
        assertThat(queryStrings).contains(
                entry("account", "gugu"),
                entry("password", "password")
        );
    }
}
