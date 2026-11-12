package org.apache.coyote.login;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class LoginParserTest {

    @Test
    void parsesUrlEncodedReservedCharactersAfterSeparatingParameters() {
        // when
        Map<String, String> parameters = LoginParser.parseQueryString("account=a%26b&password=x%3Dy");

        // then
        assertThat(parameters)
            .containsEntry("account", "a&b")
            .containsEntry("password", "x=y");
    }
}
