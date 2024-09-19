package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestBodyTest {

    @DisplayName("BufferedReader로 contentLength 만큼 읽어서 RequestBody를 만든다.")
    @Test
    void of() throws IOException {
        String stringRequestBody = "account=gugu&password=password";
        int contentLength = stringRequestBody.length();

        BufferedReader bufferedReader = new BufferedReader(new StringReader(stringRequestBody));
        Http11RequestBody requestBody = Http11RequestBody.of(bufferedReader, contentLength);

        assertThat(requestBody.getBody()).isEqualTo(stringRequestBody);
    }
}
