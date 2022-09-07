package nextstep.org.apache.coyote.util;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.util.StringParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StringParserTest {

    @Test
    @DisplayName("split 메서드는 문자열을 주어진 구분자로 파싱해서 Map으로 반환한다.")
    void split() {
        // given
        final String accountKey = "account";
        final String accountValue = "rick";
        final String passwordKey = "password";
        final String passwordValue = "1q2w3e4r";

        final String keyValueDelimiter = "=";
        final String fieldDelimiter = "&";

        final String source = accountKey + keyValueDelimiter + accountValue +
                fieldDelimiter + passwordKey + keyValueDelimiter + passwordValue;

        // when
        final Map<String, String> actual = StringParser.split(source, fieldDelimiter, keyValueDelimiter);

        // then
        assertThat(actual).hasSize(2)
                .contains(
                        entry(accountKey, accountValue),
                        entry(passwordKey, passwordValue)
                );
    }
}
