package nextstep.jwp.controller;


import nextstep.jwp.MockSocket;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpResponseStatus;
import nextstep.jwp.service.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @DisplayName("JSessionId를 셋팅한다.")
    @Test
    void setJSessionId() throws IOException {
        UUID uuid = UUID.randomUUID();
        MockSocket mockSocket = new MockSocket("GET /login HTTP/1.1 \r\n" +
                "Cookie: JSESSIONID=" + uuid + "\r\n");

        LoginController controller = new LoginController(new UserService());
        HttpResponse httpResponse = new HttpResponse(mockSocket.getOutputStream());
        controller.setJSessionId(HttpRequest.of(mockSocket.getInputStream()), httpResponse);
        httpResponse.status(HttpResponseStatus.OK);
        httpResponse.resource("/index.html");
        httpResponse.write();

        String[] splitResponse = mockSocket.output().split("\r\n");
        assertThat(splitResponse).contains("Set-Cookie: JSESSIONID=" + uuid);

    }
}