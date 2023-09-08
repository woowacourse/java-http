package org.apache.coyote.http11.request.body;

import org.apache.coyote.http11.request.RequestBody;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RequestBodyTest {

    @Nested
    class RequestBody_생성_검증 {

        @Test
        void RequestBody가_유효하다면_생성한다() {
            // given
            String body = "account=베베&password=password&email=mazzi%40woowahan.com";

            // when
            RequestBody requestBody = RequestBody.from(body);

            // then
            assertAll(
                    () -> assertThat(requestBody.getBy("account")).isEqualTo("베베"),
                    () -> assertThat(requestBody.getBy("password")).isEqualTo("password"),
                    () -> assertThat(requestBody.getBy("email")).isEqualTo("mazzi%40woowahan.com")
            );
        }

        @Test
        void RequestBody가_Null이라면_Null값으로_생성한다() {
            // given
            String body = null;

            // when
            RequestBody requestBody = RequestBody.from(body);

            // then
            assertThat(requestBody.bodies()).isEmpty();
        }

    }

}
