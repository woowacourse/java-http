package nextstep.jwp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionIDTest {

    @Test
    void value() {
        //given
        String pureSessionID = "SessionID";
        SessionIDUUIDStrategy sessionIDManualStrategy = new SessionIDUUIDStrategy(){
            private final String value = pureSessionID;
            @Override
            public String token() {
                return this.value;
            }
        };
        SessionID sessionID = SessionID.fromStrategy(sessionIDManualStrategy);

        //when
        String tokenValue = sessionID.value();

        //then
        assertThat(tokenValue).isEqualTo(pureSessionID);
    }
}