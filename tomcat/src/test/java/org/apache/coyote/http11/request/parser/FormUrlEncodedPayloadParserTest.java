package org.apache.coyote.http11.request.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("FormUrlEncodedPayloadParser 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FormUrlEncodedPayloadParserTest {

    @Test
    void formUrlEncoded_타입_데이터_파싱() {
        // given
        final PayloadParser parser = new FormUrlEncodedPayloadParser();
        final String formUrlEncodedPayload = "account=royce&password=1234";

        // when
        Map<String, String> payload = parser.parse(formUrlEncodedPayload);

        // then
        assertThat(payload).containsAllEntriesOf(Map.of(
                "account", "royce",
                "password", "1234")
        );
    }

}
