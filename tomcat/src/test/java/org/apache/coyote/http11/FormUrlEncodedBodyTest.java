package org.apache.coyote.http11;

import org.apache.coyote.http11.body.Body;
import org.apache.coyote.http11.header.Headers;
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
class FormUrlEncodedBodyTest {
    @Test
    void 폼_데이터_파싱() throws IOException {
        //given
        final byte[] bytes = "userId=echo&password=password&name=ddd".getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final Headers httpRequestHeader = Headers.parse(List.of("Content-Length: 38", "Content-Type: application/x-www-form-urlencoded"));
        //when
        final Body formUrlEncodedBody = Body.parse(httpRequestHeader.getContentLength(), httpRequestHeader.getContentType(), bufferedReader);
        final String userId = formUrlEncodedBody.getValue("userId");
        final String password = formUrlEncodedBody.getValue("password");
        final String name = formUrlEncodedBody.getValue("name");

        //then
        assertSoftly(softly -> {
            softly.assertThat(userId).isEqualTo("echo");
            softly.assertThat(password).isEqualTo("password");
            softly.assertThat(name).isEqualTo("ddd");
        });

    }


}