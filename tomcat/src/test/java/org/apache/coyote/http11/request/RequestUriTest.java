package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestUriTest {

    @Test
    void Uri_추출_테스트() {
        // given
        String uriString = "/index.html";

        // when
        RequestUri requestUri = new RequestUri(uriString);

        // then
        assertThat(uriString).isEqualTo(requestUri.getUri());
    }

    @Test
    void 메인_페이지_index_페이지_처리_테스트(){
        // given
        String uriString = "/";

        // when
        RequestUri requestUri = new RequestUri(uriString);

        // then
        assertThat("/").isEqualTo(requestUri.getUri());
    }
}
