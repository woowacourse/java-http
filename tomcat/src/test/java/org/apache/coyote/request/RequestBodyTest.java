package org.apache.coyote.request;

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
    void 요청받은_바디값을_파싱하여_생성에_성공한다() {
        // given
        final String body = "name=헤나&type=BE";

        // when
        final RequestBody requestBody = RequestBody.from(body);

        // then
        assertThatCode(() -> RequestBody.from(body))
                .doesNotThrowAnyException();
    }

    @Test
    void 바디에_키값을_가져올_수_있다() {
        // given
        final String body = "name=헤나&type=BE";

        // when
        final RequestBody requestBody = RequestBody.from(body);

        // then
        assertThat(requestBody.names()).containsExactly("name", "type");
    }

    @Test
    void 키값을_통해서_매핑된_값을_가져올_수_있다() {
        // given
        final String body = "name=헤나&type=BE";

        // when
        final RequestBody requestBody = RequestBody.from(body);

        // then
        assertAll(
                () -> assertThat(requestBody.getBodyValue("name")).isEqualTo("헤나"),
                () -> assertThat(requestBody.getBodyValue("type")).isEqualTo("BE")
        );
    }
}
