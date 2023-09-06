package nextstep.jwp.presentation;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.apache.coyote.http11.RequestFixture;
import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GetLoginControllerTest {

    @Test
    void process() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.GET_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);

        GetLoginController getLoginController = new GetLoginController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = getLoginController.process(httpRequestParser, httpResponseBuilder);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 3797")
        );
    }

    @Test
    void processIfAlreadyLogin() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.GET_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);

        GetLoginController getLoginController = new GetLoginController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        String sessionId = UUID.randomUUID().toString();
        httpRequestParser.addHeader("Cookie", "JSESSIONID=" + sessionId);
        SessionManager.add(sessionId, new User("test", "test", "test"));

        //when
        String response = getLoginController.process(httpRequestParser, httpResponseBuilder);

        //then
        Assertions.assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Location: /index.html"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }
}
