package org.apache.coyote.http11.domain.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("HttpResponse 를 생성한다.")
    void status() {
        HttpStatus httpStatus = HttpStatus.OK;
        String contentType = "text/html;charset=utf-8";
        String messageBody = "Hello, World!";

        HttpResponse httpResponse = HttpResponse.status(httpStatus)
                .header("Content-Type", contentType)
                .body(messageBody)
                .build();

        assertAll(
                () -> assertThat(httpResponse.getHttpStatus()).isEqualTo(httpStatus),
                () -> assertThat(httpResponse.getResponseHeader("Content-Type")).isEqualTo(contentType),
                () -> assertThat(httpResponse.getMessageBody()).isEqualTo(messageBody)
        );
    }
}
