package org.apache.coyote.http11.cookie;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpCookieTest {

    @Test
    void jsession_id_쿠키를_생성한다() {
        // given
        String id = "1234";

        // when
        HttpCookie httpCookie = HttpCookie.jSessionId(id);

        // then
        assertThat(httpCookie.getKey()).isEqualTo("JSESSIONID");
        assertThat(httpCookie.getValue()).isEqualTo(id);
    }

}
