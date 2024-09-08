package org.apache.coyote.http11.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    @DisplayName("최대 시간이 지나면, 만료가 된다.")
    void expired_when_over_max_inactive_interval() {
        final LocalDateTime time = LocalDateTime.now();
        final Session session = new Session(time, 5);

        final boolean result = session.isExpired(time.plusSeconds(6));
        assertThat(result).isTrue();
    }
}
