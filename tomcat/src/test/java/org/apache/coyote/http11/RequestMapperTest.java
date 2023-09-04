package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMapperTest {

    @Test
    void 요청의_첫_줄_정보로_request_mapper를_찾는다() {
        // given
        HttpRequestFirstLineInfo info = HttpRequestFirstLineInfo.from("GET /index.html HTTP/1.1");

        // when
        RequestMapper mapper = RequestMapper.findMapper(info);

        // then
        assertThat(mapper).isSameAs(RequestMapper.INDEX);

    }

    @Test
    void 요청의_첫_줄_정보로_request_mapper를_찾을_수_없으면_예외를_발생시킨다() {
        // given
        HttpRequestFirstLineInfo info = HttpRequestFirstLineInfo.from("POST /index.html HTTP/1.1");

        // expect
        assertThatThrownBy(() -> RequestMapper.findMapper(info))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 요청입니다.");
    }
}
