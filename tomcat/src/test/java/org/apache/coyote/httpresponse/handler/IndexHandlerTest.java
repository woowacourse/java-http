package org.apache.coyote.httpresponse.handler;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class IndexHandlerTest extends HandlerTestSupport {

    @Test
    void index_페이지를_보여준다() {
        // given
        final String input = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final IndexHandler indexHandler = new IndexHandler();

        // when
        final HttpResponse httpResponse = indexHandler.handle(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8"
        );

        // then
        assertThat(actual).contains(expectedHeaders);
    }
}
