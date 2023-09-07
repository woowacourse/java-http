package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.apache.coyote.http11.RequestFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class PostRegisterControllerTest {

    @Test
    void process() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.POST_REGISTER_LOGIN_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        PostRegisterController postRegisterController = new PostRegisterController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = postRegisterController.process(httpRequest, httpResponseBuilder);

        //then
        Assertions.assertAll(
                () -> assertThat(response).contains("HTTP/1.1 302 FOUND"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Location: /index.html"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }

}
