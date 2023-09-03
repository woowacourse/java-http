package org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UriComponentsBuilderTest {

    @DisplayName("URI의 Query String에서 key, value 값을 추출한다.")
    @Test
    void getQueryParams() {
        Map<String, List<String>> expected = Map.of("nickname", List.of("doy"), "keyword", List.of("uteco", "level4"));
        UriComponentsBuilder expectedUri = UriComponentsBuilder.of("/login", expected);

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.of(expectedUri.getUri());

        Map<String, List<String>> actual = uriComponentsBuilder.build().getQueryParams();
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
