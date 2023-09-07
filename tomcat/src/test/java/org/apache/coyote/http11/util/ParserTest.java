package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.request.QueryParams;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ParserTest {

    @Test
    void 주어진_문자열에서_형식에_맞게_QueryParams을_추출한다() {
        // given
        String line = "name=oing&age=24&gender=female";

        // when
        QueryParams queryParams = Parser.parseToQueryParams(line);

        // then
        assertThat(queryParams.getParam("name")).isEqualTo("oing");
        assertThat(queryParams.getParam("age")).isEqualTo("24");
        assertThat(queryParams.getParam("gender")).isEqualTo("female");
    }

    @Test
    void 주어진_문자열에서_형식에_맞게_Cookies을_추출한다() {
        // given
        String line = "name=oing; age=24; gender=female";

        // when
        Cookies cookies = Parser.parseToCookie(line);

        // then
        assertThat(cookies.getCookie("name")).isEqualTo("oing");
        assertThat(cookies.getCookie("age")).isEqualTo("24");
        assertThat(cookies.getCookie("gender")).isEqualTo("female");
    }
}
