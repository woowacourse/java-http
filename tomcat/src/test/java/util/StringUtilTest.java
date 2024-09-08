package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Test
    @DisplayName("인덱스 1부터 검색을 시작한다.")
    void find_value_with_index1() {
        final String str = "/123";
        final int index = StringUtil.findIndexStartIndexOne(str, "/");
        assertThat(index).isEqualTo(-1);
    }
}
