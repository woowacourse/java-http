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

class NotFoundControllerTest {

    private final HttpResponse httpResponse = new HttpResponse();

    @Test
    void process() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.NOT_FOUND_REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        HttpRequest httpRequest = httpRequestParser.convertToHttpRequest(inputStream);

        NotFoundController notFoundController = new NotFoundController();

        //when
        String response = notFoundController.process(httpRequest, httpResponse);

        //then
        assertAll(
                () -> assertThat(response).contains("HTTP/1.1 404 Not Found"),
                () -> assertThat(response).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(response).contains("Content-Length: 2426")
        );
    }
}
