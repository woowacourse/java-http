package org.apache.coyote.http11.response;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.http11.response.HttpContentType.TEXT_HTML;
import static org.apache.coyote.http11.response.HttpHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.response.ResponseStatus.OK;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void Content_Length를_잰다() {
        // given
        HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
        httpResponse.setResponseBody("Hello world!");

        // when
        int contentLength = httpResponse.measureContentLength();

        // then
        assertThat(contentLength).isEqualTo("Hello world!".getBytes().length);
    }

    @Test
    void 응답_본문을_반환한다() {
        // given
        HttpResponse httpResponse = new HttpResponse("HTTP/1.1");
        httpResponse.setResponseStatus(OK);
        httpResponse.setResponseBody("Hello world!");
        httpResponse.setResponseHeader(CONTENT_TYPE, TEXT_HTML.mimeTypeWithCharset(UTF_8));
        httpResponse.setResponseHeader(CONTENT_LENGTH, String.valueOf(httpResponse.measureContentLength()));

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
