package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QueryStringsTest {

    @DisplayName("key 값을 이용해 value 검색")
    @Test
    void findByKey() {
        // given
        final String key1 = "key1";
        final String value1 = "value1";

        final String key2 = "key2";
        final String value2 = "value2";

        final String text = "/login?" + key1 + "=" + value1 + "&" + key2 + "=" + value2;

        // when
        final QueryStrings queryStrings = new QueryStrings(text);

        // then
        assertThat(queryStrings.findByKey(key1)).isEqualTo(value1);
        assertThat(queryStrings.findByKey(key2)).isEqualTo(value2);
    }
}
