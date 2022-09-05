package org.apache.coyote.core.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.HttpRequestBody;
import nextstep.jwp.http.reqeust.HttpRequestLine;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("./login url 접근 시, 유저의 비밀번호가 일치하면 예외가 발생하지 않는다.")
    @Test
    void login() throws URISyntaxException, IOException {
        // given
        URI uri = new URI("/login?account=gugu&password=password");
        HttpRequestLine httpRequestLine = new HttpRequestLine("GET", uri, "HTTP/1.1");
        HttpHeader httpHeaders = new HttpHeader();
        HttpRequestBody httpRequestBody = new HttpRequestBody("");
        HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpHeaders, httpRequestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when & then
        LoginController controller = new LoginController();
        controller.service(httpRequest, httpResponse);
    }
}
