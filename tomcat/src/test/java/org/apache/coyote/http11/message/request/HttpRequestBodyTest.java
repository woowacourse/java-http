package org.apache.coyote.http11.message.request;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestBodyTest {

    @Test
    void HTTP_MESSAGE_BODY를_FORM_DATA로_파싱할_수_있다() {
        // given
        String content = "name=teo&age=25";

        // when
        RequestBody requestBody = new RequestBody(content);

        // then
        Map<String, String> formData = requestBody.getAsFormData();

        assertThat(formData)
                .containsEntry("name", "teo")
                .containsEntry("age", "25");
    }
}
