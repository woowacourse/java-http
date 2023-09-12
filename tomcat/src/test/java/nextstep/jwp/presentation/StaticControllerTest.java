package nextstep.jwp.presentation;

import coyote.http.RequestFixture;
import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.apache.coyote.http.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class StaticControllerTest {

    private final HttpResponse httpResponse = new HttpResponse();

    @Test
    void processCSS() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.CSS_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        StaticController staticController = new StaticController();

        //when
        String response = staticController.process(httpRequest, httpResponse);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/css"),
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

        //when
        String response = staticController.process(httpRequest, httpResponse);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 5564")
        );
    }

}
