package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.coyote.http11.request.RequestHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseBodyTest {

    @Test
    void redirect_시_빈_문자열을_받는다() {
        // given
        ResponseEntity responseEntity = ResponseEntity.body("redirect:index.html").status(HttpStatus.REDIRECT);

        // when
        ResponseBody responseBody = ResponseBody.of(responseEntity);

        // then
        assertThat(responseBody.value()).isEmpty();
    }
}
