package org.apache.coyote.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class UrlTest {

    @Test
    void Url_객체는_경로와_쿼리스트링을_별로로_저장한다() {
        // given
        final String rawUrl = "/login?name=gugu&password=1234";

        // when
        Url url = new Url(rawUrl);

        // then
        assertAll(
                () -> assertThat(url.getUrlPath()).isEqualTo("/login"),
                () -> assertThat(url.getQueryStrings()).usingRecursiveComparison().isEqualTo(
                        new QueryStrings("name=gugu&password=1234")
                )
        );
    }
}
