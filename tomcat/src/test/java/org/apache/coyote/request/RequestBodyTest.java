package org.apache.coyote.request;

import org.apache.coyote.common.MessageBody;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestBodyTest {

    @Test
    void 생성에_성공한다() {
        // given
        final char[] chars = "name=헤나&type=BE".toCharArray();
        final MessageBody messageBody = MessageBody.from(chars);

        // expect
        assertThatCode(() -> RequestBody.from(messageBody))
                .doesNotThrowAnyException();
    }

    @Test
    void 바디에_키값을_가져올_수_있다() {
        // given
        final char[] chars = "name=헤나&type=BE".toCharArray();
        final MessageBody messageBody = MessageBody.from(chars);

        // when
        final RequestBody actual = RequestBody.from(messageBody);

        // expect
        assertThat(actual.bodyNames()).containsExactlyInAnyOrder("name", "type");
    }

    @Test
    void 키값을_통해서_매핑된_값을_가져올_수_있다() {
        // given
        final char[] chars = "name=헤나&type=BE".toCharArray();
        final MessageBody messageBody = MessageBody.from(chars);

        // when
        final RequestBody actual = RequestBody.from(messageBody);

        // expect
        assertAll(
                () -> assertThat(actual.getBodyValue("name")).isEqualTo("헤나"),
                () -> assertThat(actual.getBodyValue("type")).isEqualTo("BE")
        );
    }
}
