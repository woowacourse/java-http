package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.apache.coyote.http11.RequestFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PostLoginControllerTest {

    @Test
    void processIfUserExist() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.POST_SUCCESS_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);

        PostLoginController postLoginController = new PostLoginController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = postLoginController.process(httpRequestParser, httpResponseBuilder);

        //then
        Assertions.assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Location: /index.html"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }

    @Test
    void processIfNonExistUser() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.POST_FAIL_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);

        PostLoginController postLoginController = new PostLoginController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = postLoginController.process(httpRequestParser, httpResponseBuilder);

        //then
        Assertions.assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Location: /401.html"),
                () -> assertThat(response).contains("Content-Length: 2426")
        );
    }

}
