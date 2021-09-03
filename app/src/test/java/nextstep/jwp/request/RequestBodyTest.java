package nextstep.jwp.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

class RequestBodyTest {

    @DisplayName("HttpRequestMessage에 Body 부분을 파싱해서 RequestBody 객체로 생성한다.")
    @Test
    void of() throws IOException {
        // given
        String body = "account=charlie&password=1234";
        String length = String.valueOf(body.getBytes().length);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(body.getBytes())));

        // when
        RequestBody requestBody = RequestBody.of(br, length);

        int expectedBodySize = 2;
        String expectedName = "account";
        String expectedValue = "charlie";

        // then
        assertThat(requestBody.getBody()).hasSize(expectedBodySize);
        assertThat(requestBody.getBody()).containsKey(expectedName);
        assertThat(requestBody.getBody()).containsValue(expectedValue);
    }

    @DisplayName("Body의 Value가 비어있어도 RequestBody 객체로 생성된다.")
    @Test
    void of2() throws IOException {
        // given
        String body = "account=";
        String length = String.valueOf(body.getBytes().length);
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream("account=".getBytes())));

        // when
        RequestBody requestBody = RequestBody.of(br, length);

        int expectedBodySize = 1;
        String expectedName = "account";
        String expectedValue = "";

        // then
        assertThat(requestBody.getBody()).hasSize(expectedBodySize);
        assertThat(requestBody.getBody()).containsKey(expectedName);
        assertThat(requestBody.getBody()).containsValue(expectedValue);
    }
}
