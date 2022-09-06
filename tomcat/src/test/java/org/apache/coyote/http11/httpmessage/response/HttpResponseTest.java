package org.apache.coyote.http11.httpmessage.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.handler.ApiHandler.ApiHandlerResponse;
import org.apache.coyote.http11.httpmessage.ContentType;
import org.apache.coyote.http11.httpmessage.request.Headers;
import org.apache.coyote.http11.httpmessage.request.Http11Version;
import org.apache.coyote.http11.view.ModelAndView;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void modelAndView를_받으면_HttpResponse객체를_응답한다() throws IOException {
        // given
        ModelAndView modelAndView = ModelAndView.of(
                ApiHandlerResponse.of(HttpStatus.OK, new LinkedHashMap<>(), "", ContentType.HTML));

        // when
        HttpResponse httpResponse = HttpResponse.of(modelAndView);

        // then
        LinkedHashMap<String, String> expectedHeaders = new LinkedHashMap<>();
        expectedHeaders.put("Content-Type", ContentType.HTML.getValue() + ";charset=utf-8 ");
        expectedHeaders.put("Content-Length", "".getBytes().length + " ");

        assertThat(httpResponse).extracting("statusLine", "headers", "responseBody")
                .containsExactly(new StatusLine(Http11Version.HTTP_11_VERSION, HttpStatus.OK),
                        new Headers(expectedHeaders),
                        "");
    }
}
