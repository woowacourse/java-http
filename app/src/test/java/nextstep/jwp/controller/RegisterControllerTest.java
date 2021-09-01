package nextstep.jwp.controller;

import nextstep.jwp.domain.Uri;
import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestBody;
import nextstep.jwp.domain.request.RequestLine;
import nextstep.jwp.domain.response.HttpResponse;
import nextstep.jwp.domain.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private static RequestLine getRegisterRequestLine = new RequestLine("GET", Uri.of("/register"), "HTTP/1.1");
    private static RequestLine postRegisterRequestLine = new RequestLine("POST", Uri.of("/register"), "HTTP/1.1");

    @DisplayName("Get 요청 테스트 - register.html을 반환한다.")
    @Test
    void doGet() throws IOException {
        //given
        HttpRequest request = HttpRequest.of(getRegisterRequestLine, null, null);
        //when
        RegisterController controller = new RegisterController();
        HttpResponse httpResponse = controller.doGet(request);
        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(httpResponse.getBody().length()).isEqualTo(4203);
    }

    @DisplayName("POST 요청 테스트")
    @Test
    void doPost() throws IOException {
        //given
        Map<String, String> bodyParamMap = new HashMap<>();
        bodyParamMap.put("account", "new_user");
        bodyParamMap.put("password", "1234");
        bodyParamMap.put("email", "email@google.com");

        RequestBody requestBody = RequestBody.of(bodyParamMap);
        HttpRequest request = HttpRequest.of(postRegisterRequestLine, null, requestBody);
        //when
        RegisterController controller = new RegisterController();
        HttpResponse httpResponse = controller.doPost(request);
        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
    }
}