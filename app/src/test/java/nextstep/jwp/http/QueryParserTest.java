package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import nextstep.jwp.http.request.HttpMethod;
import nextstep.jwp.http.request.QueryParams;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class QueryParserTest {

    @DisplayName("QueryParsing 확인")
    @ParameterizedTest
    @MethodSource
    void queryParseTest(String queryString, Map<String, String> expectedResult) {
        //given
        QueryParser queryParser = new QueryParser(queryString);
        //when
        QueryParams queryParams = queryParser.queryParams();
        //then
        assertThat(queryParams.map()).containsExactlyInAnyOrderEntriesOf(expectedResult);
    }

    static Stream<Arguments> queryParseTest(){
        return Stream.of(
            Arguments.of(
                "?a=b&b=c&c=d",
                Map.of("a", "b", "b", "c", "c", "d")
            ),
            Arguments.of(
                "배고파=나도&엄청배고파=나도나도",
                Map.of("배고파", "나도", "엄청배고파", "나도나도")
            )
        );
    }
}