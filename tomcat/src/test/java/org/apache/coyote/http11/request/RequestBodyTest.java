package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    @DisplayName("RequestBody를 정상 생성한다.")
    void testCreateValidRequestBody() {
        String body = "key=value&key2=value2";

        RequestBody requestBody = RequestBody.from(body);

        Map<String, String> params = Map.of("key", "value", "key2", "value2");
        assertThat(requestBody.getParams()).containsExactlyInAnyOrderEntriesOf(params);
    }

    @Test
    @DisplayName("빈 RequestBody를 생성한다.")
    void testCreateEmptyRequestBody() {
        String body = "";

        RequestBody requestBody = RequestBody.from(body);

        assertThat(requestBody.getParams()).isEmpty();
    }
}
