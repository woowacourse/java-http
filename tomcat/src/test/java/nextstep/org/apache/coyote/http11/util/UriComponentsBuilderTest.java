package nextstep.org.apache.coyote.http11.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.util.UriComponentsBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UriComponentsBuilderTest {

    @DisplayName("URI의 Query String에서 key, value 값을 추출한다.")
    @Test
    void getQueryParams() {
        final Map<String, List<String>> expected = Map.of("nickname", List.of("doy"), "keyword",
                List.of("uteco", "level4"));
        final UriComponentsBuilder expectedUri = UriComponentsBuilder.of("/login", expected);

        final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.of(expectedUri.getUri());

        final Map<String, List<String>> actual = uriComponentsBuilder.build().getQueryParams();
        assertThat(actual).containsExactlyInAnyOrderEntriesOf(expected);
    }
}
