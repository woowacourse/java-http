package org.apache.coyote.publisher;

import org.apache.coyote.common.Headers;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestHeaderPublisherTest {

    @Test
    void 헤더와_값이_하나의_문자열로_놓여진_상태의_목록을_이용하여_생성에_성공한다() {
        // given
        final List<String> headersWithValue = List.of(
                "Accept: text/html;charset=utf-8",
                "Connection: keep-alive"
        );

        // when
        final Headers actual = RequestHeaderPublisher.read(headersWithValue).toHeaders();

        // then
        assertThat(actual).hasFieldOrPropertyWithValue("mapping", Map.of(
                "Accept", "text/html;charset=utf-8",
                "Connection", "keep-alive"
        ));
    }
}
