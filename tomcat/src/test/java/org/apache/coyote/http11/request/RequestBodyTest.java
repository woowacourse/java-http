package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @DisplayName("객체 생성 시 문자열을 올바른 형태로 입력받아 필드에 저장한다.")
    @Test
    void should_readString_when_construct() throws IOException {
        // given
        String input = "account=ever&password=password&email=ever@woowa.com";
        BufferedReader reader = new BufferedReader(new StringReader(input));

        // when
        RequestBody requestBody = new RequestBody(reader, input.length());

        // then
        Map<String, String> queries = requestBody.parseQuery();
        assertThat(queries.get("account")).isEqualTo("ever");
        assertThat(queries.get("password")).isEqualTo("password");
        assertThat(queries.get("email")).isEqualTo("ever@woowa.com");
    }
}
