package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.TestUtil;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.ResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("/login을 요청하면 OK를 반환한다.")
    @Test
    void loginPage() throws IOException {
        Controller controller = new LoginController();
        HttpRequest request = TestUtil.createRequest("GET /login HTTP/1.1");

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.OK);
    }

    @DisplayName("/login뒤에 일치하는 회원정보가 쿼리파람이 포함된 경우 초기화면으로 리다이렉트한다.")
    @Test
    void loginSuccessRedirect() throws IOException {
        Controller controller = new LoginController();
        HttpRequest request = TestUtil.createRequest("GET /login?account=gugu&password=password HTTP/1.1");

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/index.html");
    }

    @DisplayName("/login뒤에 잘못된 회원정보가 쿼리파람이 포함된 경우 에러화면으로 리다이렉트한다.")
    @Test
    void loginFailRedirect() throws IOException {
        Controller controller = new LoginController();
        HttpRequest request = TestUtil.createRequest("GET /login?account=wrong&password=wrong HTTP/1.1");

        HttpResponse httpResponse = controller.doService(request);
        assertThat(httpResponse.getResponseStatus()).isEqualTo(ResponseStatus.FOUND);
        assertThat(httpResponse.getHttpHeader().getValueByKey("Location")).contains("/401.html");
    }
}