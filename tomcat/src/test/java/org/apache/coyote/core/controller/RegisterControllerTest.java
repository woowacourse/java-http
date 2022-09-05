package org.apache.coyote.core.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.net.URISyntaxException;
import nextstep.jwp.http.HttpHeader;
import nextstep.jwp.http.reqeust.HttpRequest;
import nextstep.jwp.http.reqeust.HttpRequestBody;
import nextstep.jwp.http.reqeust.HttpRequestLine;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("회원가입을 할 때, account, password, email 중 공백인 값이 있으면 예외를 발생한다.")
    @Test
    void register_null_password() throws URISyntaxException {
        // given
        URI uri = new URI("/register");
        HttpRequestLine httpRequestLine = new HttpRequestLine("POST", uri, "HTTP/1.1");
        HttpHeader httpHeaders = new HttpHeader();
        HttpRequestBody httpRequestBody = new HttpRequestBody(
                "account=gugu&password= &email=hkkang%40woowahan.com");
        HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpHeaders, httpRequestBody);
        HttpResponse httpResponse = new HttpResponse();

        // when & then
        RegisterController registerController = new RegisterController();
        assertThatThrownBy(() -> registerController.service(httpRequest, httpResponse))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
