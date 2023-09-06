package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class RequestBodyTest {

    @Test
    void 요청이_없을_경우_빈_바디를_반환한다() {
        // given when
        final RequestBody requestBody = RequestBody.emptyBody();

        // then
        assertThat(requestBody).isNotNull();
    }

    @Test
    void key와_value_를_확인할_수_있다() {
        // given when
        final String testInput = "key1=value1&key2=value2&key3=value3";
        final RequestBody requestBody = RequestBody.from(testInput);

        // then
        assertSoftly(softly -> {
            softly.assertThat(requestBody.get("key1")).isEqualTo("value1");
            softly.assertThat(requestBody.get("key2")).isEqualTo("value2");
            softly.assertThat(requestBody.get("key3")).isEqualTo("value3");
        });
    }

    @Test
    void 존재하지_않는_key_로_value를_찾을_경우_null_을_반환한다() {
        // given when
        final String testInput = "key1=value1&key2=value2";
        final RequestBody requestBody = RequestBody.from(testInput);

        final String notExistKey = "non-key";

        // then
        assertThat(requestBody.get(notExistKey)).isNull();
    }

    @Test
    void key_는_존재하고_value_가_빈값인_경우_null_을_반환한다() {
        // given when
        final String testInput = "key1=value1&key2";
        final RequestBody requestBody = RequestBody.from(testInput);

        // then
        assertThat(requestBody.get("key2")).isNull();
    }
}
