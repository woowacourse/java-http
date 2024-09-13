package org.apache.coyote.http11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    @DisplayName("redirect 처리할 수 있다.")
    void sendRedirect() {
        HttpResponse response = HttpResponse.createHttp11Response();
        response.sendRedirect("index.html");

        HttpStatus status = response.getStatus();
        Header header = response.getHeader();

        Assertions.assertAll(
                () -> assertThat(status).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(header.get(HttpHeaderKey.LOCATION)).hasValue("index.html")
        );
    }
}
