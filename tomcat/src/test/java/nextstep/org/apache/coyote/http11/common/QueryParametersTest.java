package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.common.QueryParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class QueryParametersTest {

    @DisplayName("Query String 에서 Query Parameter 를 모두 가져온다. 단일 조회 시 첫번째 값을 조회한다.")
    @Test
    void from() {
        final Map<String, List<String>> expected = Map.of("nickname", List.of("doy"), "keyword",
                List.of("uteco", "level4"));
        final String queryString = "nickname=doy&keyword=uteco&keyword=level4";

        final var queryParameters = QueryParameters.from(queryString);
        assertThat(queryParameters.getValues())
                .containsExactlyInAnyOrderEntriesOf(expected);
        assertThat(queryParameters.findSingleByKey("keyword"))
                .isEqualTo("uteco");
    }
}
