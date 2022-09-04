package org.apache.coyote.util;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest(name = "toMimeType 메서드는 요청 uri가 {0}일 때 MimeType으로 {1}을 반환한다.")
    @CsvSource(value = {"/ : text/html", "/index.html : text/html", "/css/styles.css : text/css",
            "/js/scripts.js : text/javascript"}, delimiterString = " : ")
    void getResourcePath(final String httpRequestUri, final String mimeType) throws IOException {
        // given

        // when
        final String actual = StringParser.toMimeType(httpRequestUri);

        // then
        assertThat(actual).isEqualTo(mimeType);
    }
}
