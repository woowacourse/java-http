package org.apache.coyote.request;

import org.apache.coyote.common.MessageBody;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MessageBodyTest {

    @Test
    void 메시지_바디를_char배열로_생성할_수_있다() {
        // given
        final char[] chars = "테스트 메시지 바디 내용".toCharArray();

        // when
        final MessageBody actual = MessageBody.from(chars);

        // then
        assertAll(
                () -> assertThat(actual.body()).isEqualTo("테스트 메시지 바디 내용"),
                () -> assertThat(actual.bodyLength()).isEqualTo(33)
        );
    }

    @Test
    void 메시지_바디를_String으로_생성할_수_있다() {
        // given
        final String value = "테스트 메시지 바디 내용";

        // when
        final MessageBody actual = MessageBody.from(value);

        // then
        assertAll(
                () -> assertThat(actual.body()).isEqualTo("테스트 메시지 바디 내용"),
                () -> assertThat(actual.bodyLength()).isEqualTo(33)
        );
    }

    @Test
    void 메시지_바디의_빈_값은_빈_문자열이다() {
        // given
        final MessageBody actual = MessageBody.empty();

        // expect
        assertAll(
                () -> assertThat(actual.body()).isEqualTo(""),
                () -> assertThat(actual.bodyLength()).isEqualTo(0)
        );
    }

    @ParameterizedTest
    @CsvSource(value = {
            "안녕하세요:15",
            "Hello:5",
            "안녕하세요Hello:20",
            "H e l l o 안 녕 하 세 요:29"
    }, delimiter = ':')
    void 메시지_바디의_내부_값의_바이트_길이를_가져올_수_있다(
            final String body,
            final int expectedLength
    ) {
        // given
        final MessageBody actual = MessageBody.from(body);

        // expect
        assertThat(actual.bodyLength()).isEqualTo(expectedLength);
    }
}
