package nextstep.org.apache.coyote.util;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.util.StringParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringParserTest {

    @Test
    @DisplayName("toMap 메서드는 쿼리스트링을 파싱해서 Map으로 반환한다.")
    void toMap() {
        // given
        final String accountKey = "account";
        final String accountValue = "rick";
        final String passwordKey = "password";
        final String passwordValue = "1q2w3e4r";

        final String queryString = accountKey + "=" + accountValue + "&" + passwordKey + "=" + passwordValue;

        // when
        final Map<String, String> actual = StringParser.toMap(queryString);

        // then
        assertThat(actual).hasSize(2)
                .contains(
                        entry(accountKey, accountValue),
                        entry(passwordKey, passwordValue)
                );
    }
}
