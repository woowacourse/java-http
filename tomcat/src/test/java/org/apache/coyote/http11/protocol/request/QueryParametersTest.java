package org.apache.coyote.http11.protocol.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryParametersTest {

    @Test
    @DisplayName("QueryParameters 를 생성한다.")
    void get() {
        QueryParameters queryParameters = new QueryParameters("name=Jinseok&age=27");

        assertThat(queryParameters.get("name")).isEqualTo("Jinseok");
        assertThat(queryParameters.get("age")).isEqualTo("27");
    }

    @Test
    void getParameters() {
        QueryParameters queryParameters = new QueryParameters("name=Jinseok&age=27");

        assertThat(queryParameters.getParameters())
                .containsExactlyInAnyOrderEntriesOf(Map.of("name", "Jinseok", "age", "27"));

    }

    @Test
    @DisplayName("쿼리스트링의 형식이 맞지 않는 QueryParameters 를 생성한다.")
    void createQeuryParametersWithInvalidQuery() {
        String query = "keyWithoutValue=&=valueWithoutKey&key==&keyValue";

        QueryParameters queryParameters = new QueryParameters(query);

        assertThat(queryParameters.getParameters()).isEmpty();
    }

}
