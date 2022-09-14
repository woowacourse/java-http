package nextstep.jwp.support;

import nextstep.jwp.http.QueryStringConverter;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class QueryStringConverterTest {

    @Test
    void queryString이_전달되면_key_value를_맵핑한_map을_반환한다() {
        // given
        final String queryString = "account=gugu&password=password";

        // when
        final Map<String, String> mapping = QueryStringConverter.convert(queryString);

        // then
        assertAll(
                () -> assertThat(mapping).containsEntry("account", "gugu"),
                () -> assertThat(mapping).containsEntry("password", "password")
        );
    }

    @Test
    void queryStirng이_null이라면_비어있는_map을_반환한다() {
        // given, when
        final Map<String, String> mapping = QueryStringConverter.convert(null);

        // then
        assertThat(mapping).isEmpty();
    }
}
