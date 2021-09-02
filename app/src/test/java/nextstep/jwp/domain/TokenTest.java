package nextstep.jwp.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TokenTest {

    @Test
    void value() {
        //given
        String pureToken = "Token";
        ManualStrategy manualStrategy = new ManualStrategy("Token");
        Token token = Token.fromStrategy(manualStrategy);

        //when
        String tokenValue = token.value();

        //then
        assertThat(tokenValue).isEqualTo(pureToken);
    }
}