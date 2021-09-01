package nextstep.jwp.framework.infrastructure.http.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("HttpCookies 단위 테스트")
class HttpCookiesTest {

    @DisplayName("from 메서드는")
    @Nested
    class Describe_from {

        @DisplayName("Cookie:로 시작하는 헤더가 없으면")
        @Nested
        class Context_not_cookie_in_header {

            @DisplayName("쿠키를 파싱하지 않는다.")
            @Test
            void it_does_not_parse_cookie() {
                // given, when
                HttpCookies httpCookies = HttpCookies.from(new ArrayList<>());

                // then
                assertThat(httpCookies)
                    .extracting("cookies")
                    .isEqualTo(new HashMap<>());
            }
        }

        @DisplayName("Cookie:로 시작하는 헤더가 존재하면")
        @Nested
        class Context_cookie_in_header {

            @DisplayName("쿠키를 파싱한다.")
            @Test
            void it_does_not_parse_cookie() {
                // given
                List<String> headers = Arrays
                    .asList("Content-Length: 16", "Cookie: JSESSIONID=123; name=kevin");

                // when
                HttpCookies httpCookies = HttpCookies.from(headers);

                // then
                assertThat(httpCookies.getAttribute("JSESSIONID"))
                    .isEqualTo("123");
                assertThat(httpCookies.getAttribute("name"))
                    .isEqualTo("kevin");
            }
        }
    }
}
