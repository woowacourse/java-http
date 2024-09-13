package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Optional;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestHeader;
import org.apache.coyote.http11.request.RequestLine;
import org.junit.jupiter.api.Test;
import org.was.controller.ResponseResult;

class RegisterControllerTest {

    @Test
    void GET_요청할_경우_회원가입_페이지_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/register", null, "HTTP/1.1");
        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), new RequestBody(null));

        RegisterController controller = new RegisterController();

        // when
        ResponseResult result = controller.doGet(request);

        // then
        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.OK),
                () -> assertThat(result.getPath()).isEqualTo("/register.html" )
        );
    }

    @Test
    void POST_요청시_본문_폼데이터_정보로_회원을_생성하고_대시보드_페이지로_리다이렉트_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/register", null, "HTTP/1.1");
        String accountName = "ted";
        String formData = "account=" + accountName + "&password=password";
        RequestBody requestBody = new RequestBody(formData);

        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), requestBody);

        RegisterController controller = new RegisterController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        Optional<User> account = InMemoryUserRepository.findByAccount(accountName);

        assertAll(
                () -> assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(result.getHeaders()).containsEntry("Location", "/index.html"),
                () -> assertThat(account.isPresent()).isTrue()
        );
    }

    @Test
    void POST_요청시_본문에_폼데이터가_없을경우_400_응답결과_반환() {
        // given
        RequestLine requestLine = new RequestLine("GET", "/register", null, "HTTP/1.1");
        RequestBody requestBody = new RequestBody(null);

        HttpRequest request = new HttpRequest(requestLine, new RequestHeader(new HashMap<>()), requestBody);

        RegisterController controller = new RegisterController();

        // when
        ResponseResult result = controller.doPost(request);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.BAD_REQUEST);
    }
}
