package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {
    @Test
    @DisplayName("RequestBody를 생성한다.")
    void createRequestBody() throws IOException {
        //given
        final String request = String.join("&",
                "key1=value1",
                "key2=value2",
                "key3=value3"
        );
        final BufferedReader bufferedReader = new BufferedReader(new StringReader(request));

        //when
        final RequestBody requestBody = RequestBody.convert(bufferedReader, request.getBytes().length);

        //then
        final Map<String, String> expected = new HashMap<>();
        expected.put("key1", "value1");
        expected.put("key2", "value2");
        expected.put("key3", "value3");

        assertThat(requestBody.getBodies()).isEqualTo(expected);
    }
}
