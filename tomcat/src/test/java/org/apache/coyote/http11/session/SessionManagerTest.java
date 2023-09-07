package org.apache.coyote.http11.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class SessionManagerTest {

    @Test
    void 세션이_저장소에_존재하면_조회에_성공한다() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final String id = "bebe";
        final Session expected = new Session(id);
        sessionManager.add(expected);

        // when
        final Session actual = sessionManager.findSession(id);

        // then
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void 세션이_저장소에_존재하지_않으면_null을_반환한다() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final String notExistId = "ditoo";

        // when
        final Session actual = sessionManager.findSession(notExistId);

        // then
        assertThat(actual).isNull();
    }
}
