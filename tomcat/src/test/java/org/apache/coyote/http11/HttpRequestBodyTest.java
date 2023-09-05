package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestBodyTest {
    @Test
    void 폼_데이터_파싱() throws IOException {
        //given
        final byte[] bytes = "userId=echo&password=password&name=ddd".getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(List.of("POST /user/create HTTP/1.1", "Host: localhost:8080", "Connection: keep-alive", "Content-Length: " + bytes.length, "Content-Type: application/x-www-form-urlencoded", "", ""));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        //when
        final HttpRequestBody httpRequestBody = HttpRequestBody.parseBody(httpRequestHeader, bufferedReader);
        final String userId = httpRequestBody.get("userId");
        final String password = httpRequestBody.get("password");
        final String name = httpRequestBody.get("name");

        //then
        assertSoftly(softly -> {
            softly.assertThat(userId).isEqualTo("echo");
            softly.assertThat(password).isEqualTo("password");
            softly.assertThat(name).isEqualTo("ddd");
        });

    }


}