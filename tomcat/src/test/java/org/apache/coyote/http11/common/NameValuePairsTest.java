package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NameValuePairsTest {

    @DisplayName("이름-값 쌍 문자열을 올바르게 파싱하여 인스턴스를 생성한다.")
    @Test
    void parseAndInstantiate() {
        // given
        String nameAndValue = "name1=value1&name2=value2&name3=value3";

        // when
        NameValuePairs nameValuePairs = new NameValuePairs(nameAndValue, "&", "=");

        // then
        assertAll(
                () -> assertThat(nameValuePairs.get("name1")).hasValue("value1"),
                () -> assertThat(nameValuePairs.get("name2")).hasValue("value2"),
                () -> assertThat(nameValuePairs.get("name3")).hasValue("value3")
        );
    }

    @DisplayName("구분자와 부합하는 이름-값 쌍이 없는 경우 빈 인스턴스를 생성한다.")
    @Test
    void invalidInput() {
        // given
        String nameAndValue = "dslf38gls239;esldkfj09htgrkjfvdk";

        // when
        NameValuePairs nameValuePairs = new NameValuePairs(nameAndValue, "&", "=");

        // then
        assertThat(nameValuePairs.getAll()).isEmpty();
    }
}
