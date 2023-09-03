package org.apache.coyote.http11.request.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("ContentTypePayloadParserMapper 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypePayloadParserMapperTest {

    @Test
    void 요청_받은_컨텐트_타입으로부터_컨텐트_타입_파서_생성() {
        // given
        final String contentType = "application/x-www-form-urlencoded";

        // when
        final ContentTypePayloadParserMapper contentTypePayloadParserMapper =
                ContentTypePayloadParserMapper.from(contentType);

        // then
        assertThat(contentTypePayloadParserMapper)
                .isEqualTo(ContentTypePayloadParserMapper.APPLICATION_X_WWW_FORM_URLENCODED);
        assertThat(contentTypePayloadParserMapper.getPayloadParser()).isInstanceOf(FormUrlEncodedPayloadParser.class);
    }

    @Test
    void 지원하지_않는_컨텐트_타입_요청시_예외_발생() {
        // given
        final String contentType = "application/json";

        // when & then
        assertThatThrownBy(() -> ContentTypePayloadParserMapper.from(contentType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지원하지 않는 Content-Type 입니다.");
    }

}
