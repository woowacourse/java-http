package org.apache.coyote.response;

import static org.apache.coyote.response.ContentType.HTML;
import static org.apache.coyote.response.StatusCode.FOUND;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("302 Found 응답시에는 Location 헤더를 포함해서 응답한다.")
    @Test
    void returnWithLocation(){
        //given
        final StatusCode statusCode = FOUND;
        final HttpResponse httpResponse = HttpResponse.of(statusCode, HTML, Location.from("/index.html"));

        //when
        final String response = httpResponse.getResponse();

        //then
        assertThat(response).contains("Location: /index.html");
    }
}
