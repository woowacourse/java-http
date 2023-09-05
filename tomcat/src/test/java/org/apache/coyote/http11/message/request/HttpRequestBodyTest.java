package org.apache.coyote.http11.message.request;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestBodyTest {

    @Test
    void HTTP_MESSAGE_BODY를_파싱할_수_있다() {
        // given
        List<String> httpBodyLines = List.of(
                "name=teo",
                "age=25"
        );

        // when
        RequestBody requestBody = RequestBody.from(httpBodyLines);

        // then
        assertThat(requestBody.get("name")).isEqualTo("teo");
        assertThat(requestBody.get("age")).isEqualTo("25");
    }
}
