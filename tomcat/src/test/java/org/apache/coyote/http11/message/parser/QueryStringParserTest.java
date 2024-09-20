package org.apache.coyote.http11.message.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringParserTest {

    @DisplayName("QueryString을 파싱할 수 있다.")
    @Test
    void parseSuccess() {
        String quertString = "account=jojo&password=1234&email=jojo@email.com";

        Map<String, String> queries = QueryStringParser.parse(quertString);

        assertAll(
                () -> assertThat(queries.size()).isEqualTo(3),
                () -> assertThat(queries.get("account")).isEqualTo("jojo"),
                () -> assertThat(queries.get("password")).isEqualTo("1234"),
                () -> assertThat(queries.get("email")).isEqualTo("jojo@email.com")
        );
    }

    @DisplayName("여러 쿼리의 구분자가 &가 아니면 예외가 발생한다.")
    @Test
    void parseFailureWithInvalidQueryDelimiter() {
        String quertString = "account=jojo password=1234 email=jojo@email.com";

        assertThatThrownBy(() -> QueryStringParser.parse(quertString))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("key, value 구분자가 =가 아니면 예외가 발생한다.")
    @Test
    void parseFailure() {
        String quertString = "account,jojo&password,1234&email,jojo@email.com";

        assertThatThrownBy(() -> QueryStringParser.parse(quertString))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
