package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.TestUtil;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("일치하는 회원정보가 requestBody에 포함된 경우 초기화면으로 리다이렉트한다.")
    @Test
    void loginSuccessRedirect() {
        Controller controller = new LoginController();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", "gugu");
        requestBody.put("password", "password");
        HttpRequest request = TestUtil.createRequest("POST /login HTTP/1.1", requestBody);

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/index.html");
    }

    @DisplayName("잘못된 회원정보가 포함된 경우 에러화면으로 리다이렉트한다.")
    @Test
    void loginFailRedirect() {
        Controller controller = new LoginController();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", "wrong");
        requestBody.put("password", "wrong");
        HttpRequest request = TestUtil.createRequest("POST /login HTTP/1.1", requestBody);

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/401.html");
    }
}