package org.apache.coyote.http11.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Nested
    class 세션_생성을_검증한다 {
        @Test
        void 세션_값이_정상이라면_생성한다() {
            // given
            final String id = "JSESSIONID";

            // when
            Session session = Session.from(id);

            // then
            assertThat(session.getId()).isEqualTo(id);
        }
    }

}
