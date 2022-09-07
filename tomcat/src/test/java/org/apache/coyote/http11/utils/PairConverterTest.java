package org.apache.coyote.http11.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PairConverterTest {

    @DisplayName("사용자 데이터를 map에 저장한다.")
    @Test
    void toMapByUserData() {
        final String target = "account=gugu&password=password";

        final Map<String, String> map = PairConverter.toMap(target, "&", "=");
        assertThat(map.get("account")).isEqualTo("gugu");
        assertThat(map.get("password")).isEqualTo("password");
    }

    @DisplayName("header 데이터를 map에 저장한다.")
    @Test
    void validatePairDataFormat() {
        final String target = "Accept: text/css,*/*;q=0.1 \n"
                + "Connection: keep-alive ";

        final Map<String, String> map = PairConverter.toMap(target, "\n", ": ");
        assertThat(map.get("Accept")).isEqualTo("text/css,*/*;q=0.1 ");
        assertThat(map.get("Connection")).isEqualTo("keep-alive ");
    }
}
