package org.apache.coyote.http11;

import org.apache.coyote.http11.exception.BadRequestException;
import org.apache.coyote.http11.request.QueryParameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryParametersTest {

    @Test
    void 중복_키는_모두_보존하고_get은_첫_값을_반환한다() {
        final QueryParameters params = QueryParameters.from("hobby=game&hobby=movie");

        assertThat(params.get("hobby")).hasValue("game");
        assertThat(params.getAll("hobby")).containsExactly("game", "movie");
    }

    @ParameterizedTest
    @ValueSource(strings = {"=value", "=", "&=&", "+=1", "%20=1"})
    void 이름이_비어_있으면_무시한다(final String query) {
        assertThat(QueryParameters.from(query).isEmpty()).isTrue();
    }

    @Test
    void 값이_없는_플래그_파라미터는_빈_문자열로_저장한다() {
        final QueryParameters params = QueryParameters.from("debug&verbose=");

        assertThat(params.get("debug")).hasValue("");
        assertThat(params.get("verbose")).hasValue("");
    }

    @Test
    void 파라미터_개수가_한도를_넘으면_거부한다() {
        final String query = IntStream.rangeClosed(1, 1_001)
                .mapToObj(i -> "a" + i + "=1")
                .collect(Collectors.joining("&"));

        assertThatThrownBy(() -> QueryParameters.from(query))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 반환된_리스트는_수정할_수_없다() {
        final QueryParameters params = QueryParameters.from("a=1");

        assertThatThrownBy(() -> params.getAll("a").add("2"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void null_이름으로_조회하면_빈_값을_반환한다() {
        assertThat(QueryParameters.from("a=1").get(null)).isEmpty();
    }
}