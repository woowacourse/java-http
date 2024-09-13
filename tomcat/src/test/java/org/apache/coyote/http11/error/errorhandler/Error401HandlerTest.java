package org.apache.coyote.http11.error.errorhandler;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import support.BaseHttpTest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Error401HandlerTest extends BaseHttpTest {

    @DisplayName("401 에러는 401.html을 반환하도록 핸들링한다")
    @Test
    void handle() throws URISyntaxException, IOException {
        Error401Handler errorHandler = new Error401Handler();

        HttpResponse response = errorHandler.handle();

        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = resolve200Response("html", resource);
        assertThat(response.serialize()).isEqualTo(expected);
    }
}
