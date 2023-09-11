package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.coyote.http11.controller.ErrorController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticController;
import org.apache.coyote.http11.request.HttpRequest;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @DisplayName("Request에 해당하는 컨트롤러를 찾아온다.")
    @Test
    void findController() throws IOException {
        //given
        final RequestMapping requestMapping = new RequestMapping();

        //when
        final String index = "GET /index.html HTTP/1.1";
        final String login = "GET /login HTTP/1.1";
        final String register = "GET /register HTTP/1.1";
        final String error = "GET /sd HTTP/1.1";

        final HttpRequest indexRequest = new HttpRequest(getBufferedReader(index));
        final HttpRequest loginRequest = new HttpRequest(getBufferedReader(login));
        final HttpRequest registerRequest = new HttpRequest(getBufferedReader(register));
        final HttpRequest errorRequest = new HttpRequest(getBufferedReader(error));

        //then
        SoftAssertions.assertSoftly(
                soft -> {
                    soft.assertThat(requestMapping.getController(indexRequest)).isInstanceOf(
                            StaticController.class);
                    soft.assertThat(requestMapping.getController(loginRequest)).isInstanceOf(
                            LoginController.class);
                    soft.assertThat(requestMapping.getController(registerRequest)).isInstanceOf(
                            RegisterController.class);
                    soft.assertThat(requestMapping.getController(errorRequest)).isInstanceOf(
                            ErrorController.class);
                }
        );
    }

    private BufferedReader getBufferedReader(final String request) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(request.getBytes()))) {
            boolean fist = true;
            @Override
            public String readLine() {
                if (fist) {
                    fist = false;
                    return request;
                }
                return "";
            }
        };
    }
}
