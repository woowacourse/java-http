package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.handler.SignupRequestHandler;
import java.util.List;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.request.Http11RequestHeaders;
import org.apache.coyote.http11.request.Http11RequestLine;
import org.apache.coyote.http11.response.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SignupRequestHandlerTest {

    private RequestHandler handler = new SignupRequestHandler();

    @DisplayName("회원가입을 하면 /index.html로 리다이렉트 한다.")
    @Test
    void handle1() throws Exception {
        String body = "account=test&email=test@test&password=test";
        Http11RequestLine requestLine = new Http11RequestLine("POST /register HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Content-Length: " + body.getBytes().length));
        Http11RequestBody requestBody = new Http11RequestBody(body);
        HttpRequest request = new Http11Request(requestLine, headers, requestBody);
        HttpResponse response = new Http11Response();

        handler.handle(request, response);

        String actual = response.getResponseMessage();
        String expectedResponseLine = "HTTP/1.1 302 Found";
        String expectedLocation = "/index.html";
        assertThat(actual).contains(expectedResponseLine);
        assertThat(actual).contains(expectedLocation);
    }

    @DisplayName("회원가입에 실패하면, 예외가 발생한다.")
    @Test
    void handle2() {
        //구구 계정 이미 있음
        String body = "account=gugu&email=test@test&password=password";
        Http11RequestLine requestLine = new Http11RequestLine("POST /register HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Content-Length: " + body.getBytes().length));
        Http11RequestBody requestBody = new Http11RequestBody(body);
        HttpRequest request = new Http11Request(requestLine, headers, requestBody);
        HttpResponse response = new Http11Response();

        assertThatThrownBy(() -> handler.handle(request, response))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("java.lang.IllegalStateException: 이미 존재하는 아이디 입니다.");

    }
}
