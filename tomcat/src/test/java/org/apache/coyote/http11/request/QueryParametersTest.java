package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.QueryParameters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class QueryParametersTest {

    @Test
    void query_parameter가_있을_경우_true를_반환한다() {
        // given
        String uri = "/nextstep.txt?name=eden";
        QueryParameters queryParameters = QueryParameters.of(uri);

        // when & then
        assertThat(queryParameters.isEmpty()).isFalse();
    }

    @Test
    void query_parameter가_없을_경우_false를_반환한다() {
        // given
        String uri = "/nextstep.txt";
        QueryParameters queryParameters = QueryParameters.of(uri);

        // when & then
        assertThat(queryParameters.isEmpty()).isTrue();
    }

    @Test
    void query_parameter로_account와_password가_들어오면_저장한다() {
        // given
        String uri = "/test?account=eden&password=eden123";
        QueryParameters queryParameters = QueryParameters.of(uri);

        // when & then
        Assertions.assertAll(
                () -> assertThat(queryParameters.get("account")).isEqualTo("eden"),
                () -> assertThat(queryParameters.get("password")).isEqualTo("eden123")
        );
    }

    @Test
    void query_parameter로_value가_없는_값이_들어오면_null을_넣는다() {
        // given
        String uri = "/test?account=";

        // when
        QueryParameters queryParameters = QueryParameters.of(uri);

        // then
        assertThat(queryParameters.get("account")).isNull();
    }
}
