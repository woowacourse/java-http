package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CookieTest {

    @Test
    void 쿠키_파싱_테스트(){
        // given
        String cookies = "JSESSIONID=j1234; user=1; name=kiara; expired=false";

        // when
        Cookie cookie = new Cookie(cookies);
        Map<String, String> params = cookie.getParams();

        // then
        assertSoftly(softly -> {
                softly.assertThat(params.get("JSESSIONID")).isEqualTo("j1234");
                softly.assertThat(params.get("user")).isEqualTo("1");
                softly.assertThat(params.get("name")).isEqualTo("kiara");
                softly.assertThat(params.get("expired")).isEqualTo("false");
        });
    }

    @Test
    void 세션ID_존재_확인_테스트() {
        // given
        String cookies = "JSESSIONID=j1234; user=1; name=kiara; expired=false";

        // when
        Cookie cookie = new Cookie(cookies);

        // when
        assertThat(cookie.hasSessionId()).isTrue();
    }
}
