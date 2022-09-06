package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpCookieTest {

    @Test
    void cookie값을_받아_HttpCookie를_생성한다() {
        HttpCookie expected = new HttpCookie(Map.of("yummy_cookie", "choco",
                "tasty_cookie", "strawberry",
                "JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
        HttpCookie actual = HttpCookie.from("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 빈_HttpCookie를_생성한다() {
        HttpCookie expected = new HttpCookie(new HashMap<>());
        HttpCookie actual = HttpCookie.empty();

        assertThat(actual).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46,false", "tasty_cookie=strawberry,true"})
    void session_id가_비어있는지_확인한다(final String input, final boolean expected) {
        HttpCookie httpCookie = HttpCookie.from(input);
        boolean actual = httpCookie.isEmptySessionId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void response에_담길_cookie정보를_생성한다() {
        String expected = "Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ";
        HttpCookie httpCookie = new HttpCookie(Map.of("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
        String actual = httpCookie.toHeaderFormat();

        assertThat(actual).isEqualTo(expected);
    }
}
