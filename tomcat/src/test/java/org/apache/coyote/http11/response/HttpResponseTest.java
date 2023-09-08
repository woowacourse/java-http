package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.ResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void 응답_본문을_반환한다() {
        // given
        HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
        httpResponse.setResponseMessage(OK, "Hello world!");

        // when
        String message = httpResponse.responseMessage();

        // then
        assertThat(message).isEqualTo(
                String.join(
                        System.lineSeparator(),
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: 12 ",
                        "",
                        "Hello world!"
                )
        );
    }
}
