package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

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

    @Test
    void 쿼리_추출_테스트(){
        // given
        String uriString = "/index.html?userId=1&password=1234";

        // when
        RequestUri requestUri = new RequestUri(uriString);
        Map<String, String> queries = requestUri.getQueries();

        // then
        assertSoftly(softly -> {
                softly.assertThat(queries.get("userId")).isEqualTo("1");
                softly.assertThat(queries.get("password")).isEqualTo("1234");
        });
    }

    @Test
    void 쿼리_존재시_Uri_추출_테스트(){
        // given
        String uriString = "/index.html?userId=1&password=1234";

        // when
        RequestUri requestUri = new RequestUri(uriString);

        // then
        assertThat("/index.html").isEqualTo(requestUri.getUri());
    }
}
