package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("HttpResponse의 출력값을 테스트한다.")
    @Test
    void toStringTest() {
        // given
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusLine(HttpStatus.OK);
        httpResponse.setBody("Test Body");

        // when
        String responseString = httpResponse.toString();

        // then
        assertAll(
                () -> assertThat(responseString).contains("HTTP/1.1 200 OK"),
                () -> assertThat(responseString).contains("Content-Length"),
                () -> assertThat(responseString).contains("Test Body")
        );
    }
}
