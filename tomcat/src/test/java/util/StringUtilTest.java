package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Test
    @DisplayName("인덱스 1부터 검색을 시작한다.")
    void find_value_with_index1() {
        final String str = "/123";
        final int index = StringUtil.findIndexStartIndexOne(str, "/");
        assertThat(index).isEqualTo(-1);
    }

    @Test
    @DisplayName("컬렉션 중 빈 문자열이 있는지 검사한다.")
    void filter_blank_with_collection() {
        final Collection<String> collections = List.of("str", "str1", "");

        assertThat(StringUtil.filterBlank(collections)).isTrue();
    }

    @Test
    @DisplayName("구분자로 두 값으로 나눈다.")
    void split_bi_value_with_delimiter() {
        final String str = "abc:def";
        final String delimiter = ":";
        final var biValue = StringUtil.splitBiValue(str, delimiter);
        assertThat(biValue.first()).isEqualTo("abc");
        assertThat(biValue.second()).isEqualTo("def");
    }

    @Test
    @DisplayName("구분자로 두 값을 합친다.")
    void combine_bi_value_with_delimiter() {
        final String str1 = "abc";
        final String str2 = "def";
        final String delimiter = ":";
        final var combineValue = StringUtil.combineWithDelimiter(new BiValue<>(str1, str2), delimiter);
        assertThat(combineValue).isEqualTo("abc:def");
    }

    @Test
    @DisplayName("빈 값이면 빈 문자열을 반환한다.")
    void return_blank_if_null() {
        final String str = null;
        assertThat(StringUtil.blankIfNull(str)).isBlank();
    }

    @Test
    @DisplayName("접미사가 없다면, 추가한다.")
    void add_suffix_if_not_exist_suffix() {
        final String str = "abc";
        final String suffix = "def";
        assertThat(StringUtil.addSuffixIfNotEndSuffix(str, suffix)).isEqualTo("abcdef");
    }

    @Test
    @DisplayName("접미사가 이미 있다면, 추가하지 않는다.")
    void identify_if_exist_suffix() {
        final String str = "abcdef";
        final String suffix = "def";
        assertThat(StringUtil.addSuffixIfNotEndSuffix(str, suffix)).isEqualTo("abcdef");
    }
}
