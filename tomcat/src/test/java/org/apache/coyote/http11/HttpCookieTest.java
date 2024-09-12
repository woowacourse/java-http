package org.apache.coyote.http11;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키 값들을 한 줄의 문자열로 반환한다")
    @Test
    void getResponse() {
        // given
        HttpCookie httpCookie = new HttpCookie(Map.of(
                "JSESSIONID", "id",
                "newJeans", "내가만든쿠키",
                "웰시", "코키"
        ));

        // when
        String actual = httpCookie.getResponse();

        // then
        String expected1 = "JSESSIONID=id";
        String expected2 = "newJeans=내가만든쿠키";
        String expected3 = "웰시=코키";
        assertThat(actual)
                .contains(expected1)
                .contains(expected2)
                .contains(expected3);
    }
}
