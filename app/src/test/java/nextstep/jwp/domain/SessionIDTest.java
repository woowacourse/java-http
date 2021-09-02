package nextstep.jwp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionIDTest {

    @Test
    void value() {
        //given
        String pureToken = "Token";
        SessionIDManualStrategy sessionIDManualStrategy = new SessionIDManualStrategy("Token");
        SessionID sessionID = SessionID.fromStrategy(sessionIDManualStrategy);

        //when
        String tokenValue = sessionID.value();

        //then
        assertThat(tokenValue).isEqualTo(pureToken);
    }
}