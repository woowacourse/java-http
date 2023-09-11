package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;
import org.apache.coyote.http11.httpmessage.request.HttpRequest;
import org.apache.coyote.http11.httpmessage.request.QueryString;
import org.apache.coyote.http11.httpmessage.request.RequestBody;
import org.apache.coyote.http11.httpmessage.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RootControllerTest {

    @Test
    @DisplayName("main 페이지를 반환한다.")
    void load_root_page() {
        // given
        final HttpRequest httpRequest = new HttpRequest(
            HttpMethod.GET,
            "/",
            "HTTP/1.1",
            new HttpHeader(new HashMap<>()),
            new QueryString(new HashMap<>()),
            new RequestBody(new HashMap<>()));

        final HttpResponse httpResponse = new HttpResponse();
        final RootController rootController = new RootController();

        // when
        rootController.doGet(httpRequest, httpResponse);

        // then
        final String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 12 \r\n" +
            "\r\n" +
            "Hello world!";

        assertThat(httpResponse.makeToString()).isEqualTo(expected);
    }
}
