package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class IndexHandlerTest extends HandlerTestSupport {

    @Test
    void index_페이지를_보여준다() {
        // given
        final String input = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*";
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final IndexHandler indexHandler = new IndexHandler();

        // when
        final HttpResponse httpResponse = indexHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n";

        // then
        assertThat(actual).contains(expectedHeader);
    }
}
