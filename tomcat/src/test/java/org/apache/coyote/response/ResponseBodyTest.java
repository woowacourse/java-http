package org.apache.coyote.response;

import org.apache.coyote.request.RequestBody;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseBodyTest {

    @Test
    void 요청_바디값을_키_값_형태로_파싱하여_생성에_성공한다() {
        // given
        final String requestBodyValue = "account=hyena&type=BE";

        // when
        final RequestBody requestBody = RequestBody.from(requestBodyValue);

        // then
        final List<String> actualNames = requestBody.names();
        final String actualAccountValue = requestBody.getBodyValue("account");
        final String actualTypeValue = requestBody.getBodyValue("type");

        assertAll(
                () -> assertThat(actualNames).containsExactlyInAnyOrder("account", "type"),
                () -> assertThat(actualAccountValue).isEqualTo("hyena"),
                () -> assertThat(actualTypeValue).isEqualTo("BE")
        );
    }
}
