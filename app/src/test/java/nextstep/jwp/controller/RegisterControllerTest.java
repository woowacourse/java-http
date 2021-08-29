package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nextstep.TestUtil;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ResponseStatus;
import nextstep.jwp.http.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("회원가입이 정상적으로 처리되면 초기화면으로 리다이렉트한다.")
    @Test
    void loginSuccessRedirect() {
        Controller controller = new RegisterController();
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", "aaron");
        requestBody.put("email", "aaron@email.com");
        requestBody.put("password", "password");
        HttpRequest request = TestUtil.createRequest("POST /register HTTP/1.1", requestBody);

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(InMemoryUserRepository.findByAccount("aaron")).isPresent();
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/index.html");
    }

}