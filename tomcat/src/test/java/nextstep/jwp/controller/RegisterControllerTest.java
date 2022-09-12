package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    Map<String, String> requestHeaders = new HashMap<>();

    @BeforeEach
    void setUp() {
        requestHeaders.put("Host", "localhost:8080");
        requestHeaders.put("Connection", "keep-alive");
    }

    @DisplayName("GET 호출 시 register.html 을 보여준다.")
    @Test
    void showLoginHtmlWhenUserNotExistedInSession() throws Exception {
        RequestLine startLine = RequestLine.from("GET /register HTTP/1.1 ");
        HttpHeaders headers = new HttpHeaders(requestHeaders);

        HttpRequest httpRequest = new HttpRequest(startLine, headers, null);

        LoginController loginController = new LoginController();

        HttpResponse httpResponse = loginController.doGet(httpRequest, new HttpResponse());

        assertAll(
                () -> assertThat(httpResponse.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(httpResponse.getHeaders().getContentType()).isEqualTo("text/html;charset=utf-8")
        );
    }

}
