package nextstep.jwp.presentation;

import org.apache.coyote.http11.HttpRequestParser;
import org.apache.coyote.http11.HttpResponseBuilder;
import org.apache.coyote.http11.RequestFixture;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GetRegisterControllerTest {

    @Test
    void process() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.GET_REGISTER_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);

        GetRegisterController getRegisterController = new GetRegisterController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = getRegisterController.process(httpRequestParser, httpResponseBuilder);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 4319")
        );
    }
}
