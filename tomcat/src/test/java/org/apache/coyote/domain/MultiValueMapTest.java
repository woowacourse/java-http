package org.apache.coyote.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import util.MultiValueMap;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MultiValueMapTest {

    @Test
    void multiValueMap_은_키에_대한_제일_마지막에_들어간_값을_가져올_수_있다() {
        // given
        final String key = "Key";
        final String value1 = "Value1";
        final String value2 = "Value2";
        final String value3 = "Value3";
        final MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();

        // when
        multiValueMap.put(key, value1);
        multiValueMap.put(key, value2);
        multiValueMap.put(key, value3);

        // then
        assertThat(multiValueMap.getRecentValue(key)).isEqualTo(value3);
    }

    @Test
    void multiValueMap_키에_대한_모든_값을_가져올_수_있다() {
        // given
        final String key = "Key";
        final String value1 = "Value1";
        final String value2 = "Value2";
        final String value3 = "Value3";
        final MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();

        // when
        multiValueMap.put(key, value1);
        multiValueMap.put(key, value2);
        multiValueMap.put(key, value3);

        // then
        assertThat(multiValueMap.getValues(key)).isEqualTo(
                List.of(value1, value2, value3)
        );
    }

    @Test
    void multiValueMap_없는_키에_대한_값을_가져올_시_null을_반환한다() {
        assertThat(new MultiValueMap<String, String>().getValues("Key")).isEqualTo(null);
    }

    @Test
    void multiValueMap_은_값을_넣은_순서대로_리스트에_저장한다() {
        // given
        final String key = "Key";
        final List<String> values1 = List.of("Value1", "Value2", "Value3");
        final String value = "Value4";
        final List<String> values2 = List.of("Value5", "Value6", "Value7");
        final MultiValueMap<String, String> multiValueMap = new MultiValueMap<>();

        // when
        multiValueMap.putAll(key, values1);
        multiValueMap.put(key, value);
        multiValueMap.putAll(key, values2);

        // then
        assertThat(multiValueMap.getValues(key)).isEqualTo(
                List.of("Value1", "Value2", "Value3", "Value4", "Value5", "Value6", "Value7")
        );
    }
}
