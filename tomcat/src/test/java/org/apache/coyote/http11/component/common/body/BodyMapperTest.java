package org.apache.coyote.http11.component.common.body;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.function.Function;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;

class BodyMapperTest {

    @ParameterizedTest
    @DisplayName("컨텐트 타입으로 Body를 매핑한다.")
    @EnumSource(value = BodyMapper.class, mode = Mode.EXCLUDE, names = "FORM_URL_ENCODED")
    void map_content_type_to_body(final BodyMapper bodyMapper) {
        // given
        final String plaintext = bodyMapper.getMimeType();

        // when
        final Function<String, Body> mapping = BodyMapper.getMapping(plaintext);

        // then
        assertThat(mapping.apply("")).isInstanceOf(TextTypeBody.class);
    }

    @ParameterizedTest
    @DisplayName("컨텐트 타입으로 Body를 매핑한다.- x-www-form-url-encoded")
    @EnumSource(value = BodyMapper.class, mode = Mode.INCLUDE, names = "FORM_URL_ENCODED")
    void map_content_type_to_form_url_encoded_body(final BodyMapper bodyMapper) {
        // given
        final String plaintext = bodyMapper.getMimeType();

        // when
        final Function<String, Body> mapping = BodyMapper.getMapping(plaintext);

        // then
        assertThat(mapping.apply("")).isInstanceOf(FormUrlEncodeBody.class);
    }
}
