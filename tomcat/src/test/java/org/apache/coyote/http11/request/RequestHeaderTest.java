package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeaderTest {
    @Test
    @DisplayName("RequestHeader를 생성한다.")
    void convert() {
        // given
        String input = "Key1: value1\r\n"
                + "Key2: value2\r\n"
                + "";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(input));

        // when
        RequestHeader requestHeader = RequestHeader.convert(bufferedReader);

        // then
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("Key1", "value1");
        expectedMap.put("Key2", "value2");

        assertThat(requestHeader.getHeader()).isEqualTo(expectedMap);
    }

}
