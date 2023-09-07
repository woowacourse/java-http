package nextstep.jwp.presentation;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.apache.coyote.http.HttpResponseBuilder;
import coyote.http.RequestFixture;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StaticControllerTest {

    @Test
    void processCSS() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.CSS_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        StaticController staticController = new StaticController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = staticController.process(httpRequest, httpResponseBuilder);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/css;charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 211991")
        );
    }

    @Test
    void processHTML() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.HTML_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        StaticController staticController = new StaticController();
        HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();

        //when
        String response = staticController.process(httpRequest, httpResponseBuilder);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }

}
