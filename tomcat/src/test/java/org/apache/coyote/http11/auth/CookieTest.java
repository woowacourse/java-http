package org.apache.coyote.http11.auth;

import java.util.List;
import org.apache.coyote.http11.request.body.RequestBody;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CookieTest {

    @Nested
    class 쿠키_생성_검증 {
        @Test
        void 쿠키_값이_유효하다면_생성한다() {
            // given
            List<String> elements = List.of("JSESSIONID=5669d1ea-eb70-4881-b17e-c7ab8cfd10b8");

            // when
            Cookie cookie = Cookie.from(elements);

            // then
            assertThat(cookie.get("JSESSIONID")).isEqualTo("5669d1ea-eb70-4881-b17e-c7ab8cfd10b8");
        }
    }

    @Nested
    class 쿠키_저장_검증 {
        @Test
        void key와_value를_입력받아_저장한다() {
            // given
            final Cookie cookie = Cookie.from(null);

            // when
            cookie.put("woowacourse", "베베");

            // then
            assertThat(cookie.get("woowacourse")).isEqualTo("베베");
        }

        @Test
        void key를_입력받아_값을_반환한다() {
            // given
            final String body = "account=bebe&password=password&email=rltgjqmduftlagl@gmail.com";
            final RequestBody requestBody = RequestBody.from(body);

            // expect
            assertAll(
                    () -> assertThat(requestBody.getBy("account")).isEqualTo("bebe"),
                    () -> assertThat(requestBody.getBy("password")).isEqualTo("password"),
                    () -> assertThat(requestBody.getBy("email")).isEqualTo("rltgjqmduftlagl@gmail.com")
            );
        }
    }
}
