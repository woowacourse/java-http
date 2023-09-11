package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

class HttpRequestBodyTest {

    @Test
    @DisplayName("")
    void parse() throws IOException {
        //given
        String body = "account=gugu&password=password";
        final HttpRequestBody requestBody = HttpRequestBody.from("30", new BufferedReader(new StringReader(body)));
        final Map<String, String> actual = Map.of("account", "gugu", "password", "password");

        //when
        final Map<String, String> expected = requestBody.parse();

        //then
        Assertions.assertThat(expected)
                .usingDefaultComparator()
                .usingRecursiveComparison()
                .isEqualTo(actual);
    }

}