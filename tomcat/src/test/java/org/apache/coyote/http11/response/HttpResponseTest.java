package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.ExtensionType;
import org.apache.coyote.http11.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("Response에 Cookie를 추가할 수 있다.")
    @Test
    void setCookie() {
        // given
        final HttpResponse httpResponse = new HttpResponse();
        final ResponseBody responseBody = ResponseBody.of(ExtensionType.HTML.getExtension(), "test");
        httpResponse.setResponseHeader(ResponseHeader.from(responseBody));

        final HttpCookie httpCookie = new HttpCookie();

        // when
        httpResponse.setCookie(httpCookie);

        // then
        assertThat(httpResponse.getResponseHeaders().getValue("Set-Cookie")).isNotNull();
    }
}
