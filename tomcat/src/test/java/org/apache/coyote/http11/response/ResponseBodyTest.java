package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpExtensionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    @DisplayName("content를 통해 html 타입의 ResponseBody를 생성할 수 있다.")
    @Test
    void html() {
        // given
        final String test = "test";
        final ResponseBody responseBody = ResponseBody.html(test);

        // when & then
        assertAll(
                () -> assertThat(responseBody.getHttpExtensionType()).isEqualTo(HttpExtensionType.HTML),
                () -> assertThat(responseBody.getContent()).isEqualTo(test)
        );
    }

    @DisplayName("extension과 content를 통해 해당하는 타입의 RequestBody를 생성할 수 있다.")
    @Test
    void of() {
        // given
        final String test = "test";
        final ResponseBody responseBody = ResponseBody.of(".css", test);

        // when & then
        assertAll(
                () -> assertThat(responseBody.getHttpExtensionType()).isEqualTo(HttpExtensionType.CSS),
                () -> assertThat(responseBody.getContent()).isEqualTo(test)
        );
    }

    @DisplayName("RequestBody의 content 길이를 반환할 수 있다.")
    @Test
    void getLength() {
        // given
        final String content = "This Content's Length is 27";
        final int expected = content.length();

        // when
        final ResponseBody responseBody = ResponseBody.of(".css", content);
        final int actual = responseBody.getLength();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
