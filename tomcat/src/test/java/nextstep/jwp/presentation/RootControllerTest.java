package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import coyote.http.RequestFixture;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RootControllerTest {

    private final HttpResponse httpResponse = new HttpResponse();

    @Test
    void process() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.ROOT_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        RootController rootController = new RootController();

        //when
        String response = rootController.process(httpRequest, httpResponse);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 12"),
                () -> assertThat(response).contains("Hello world!")
        );
    }

}
